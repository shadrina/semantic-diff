package ru.nsu.diff.engine.transforming

import com.intellij.configurationStore.unwrapState
import ru.nsu.diff.engine.transforming.EditOperationType.*
import ru.nsu.diff.engine.util.DeltaTreeElement
import ru.nsu.diff.engine.util.InputTuple
import ru.nsu.diff.engine.util.LongestCommonSubsequence
import ru.nsu.diff.engine.util.Queue

object EditScriptGenerator {
    private lateinit var inputTuple: InputTuple
    private lateinit var script: EditScript

    fun generateScript(inputTuple: InputTuple) : EditScript? {
        this.script = EditScript()
        this.inputTuple = inputTuple

        val queue = Queue<DeltaTreeElement>()
        queue.enqueue(inputTuple.T2)
        bfs(queue, mutableListOf())
        inputTuple.T1.deleteRedundant()

        return if (treesAreIdentical(inputTuple.T1, inputTuple.T2)) script else null
    }

    private fun treesAreIdentical(root1: DeltaTreeElement, root2: DeltaTreeElement) =
            root1.text.removeWhiteSpace() == root2.text.removeWhiteSpace()
    private fun String.removeWhiteSpace() = this
            .replace(Regex("[\r\n ]"), "")

    private fun bfs(queue: Queue<DeltaTreeElement>, visited: MutableList<DeltaTreeElement>) {
        while (!queue.isEmpty()) {
            val curr = queue.dequeue() ?: return
            visited.add(curr)

            val currParent = curr.parent
            var partner = inputTuple.binaryRelation.getPartner(curr)

            // Insert-phase
            if (partner == null && currParent != null) {
                val newNode = curr.copy()
                val dstNode = inputTuple.binaryRelation.getPartner(currParent)
                val ranges = Pair(null, curr.linesRange)
                val insertOperation = EditOperation(INSERT, newNode, dstNode, curr.findPosition(), ranges)
                script.addAndPerform(insertOperation)
                partner = newNode
                matchAllNodes(newNode, curr)

            // Move-phase
            } else if (partner != null && currParent != null) {
                val partnerParent = partner.parent
                if (partnerParent != null && !inputTuple.binaryRelation.contains(Pair(partnerParent, currParent))) {
                    val ranges = Pair(partner.linesRange, curr.linesRange)
                    val moveOperation = EditOperation(MOVE, partner, partnerParent, curr.findPosition(), ranges)
                    script.addAndPerform(moveOperation)
                }
            }
            // Align-phase
            alignChildren(partner!!, curr)

            curr.children.forEach { queue.enqueue(it) }
        }
    }

    private fun DeltaTreeElement.deleteRedundant() {
        val deleteOps = mutableListOf<EditOperation>()
        this.children.forEach {
            if (!inputTuple.binaryRelation.containsPairFor(it)) {
                val ranges = Pair(it.linesRange, null)
                val deleteOperation = EditOperation(DELETE, it, null, null, ranges)
                deleteOps.add(deleteOperation)
            } else it.deleteRedundant()
        }
        deleteOps.forEach {
            script.addAndPerform(it)
        }
    }

    private fun matchAllNodes(x: DeltaTreeElement, y: DeltaTreeElement) {
        inputTuple.binaryRelation.add(x, y)

        var index = 0
        var nextChildX = x.children.getOrNull(index)
        var nextChildY = y.children.getOrNull(index)
        while (nextChildX != null && nextChildY != null) {
            matchAllNodes(nextChildX, nextChildY)

            index++
            nextChildX = x.children.getOrNull(index)
            nextChildY = y.children.getOrNull(index)
        }
    }

    private fun DeltaTreeElement.copy() : DeltaTreeElement {
        val newDelta = DeltaTreeElement(this.type, this.text)
        this.children.forEach { newDelta.addChild(it.copy()) }
        return newDelta
    }

    private fun DeltaTreeElement.findPosition() : Int = this.parent?.indexOf(this) ?: 0

    private fun alignChildren(elem1: DeltaTreeElement, elem2: DeltaTreeElement) {
        val S1 = elem1.children.filter { inputTuple.binaryRelation.containsPairFor(it) }
        val S2 = elem2.children.filter { inputTuple.binaryRelation.containsPairFor(it) }
        val S = LongestCommonSubsequence.find(
                S1,
                S2,
                fun (x, y) = inputTuple.binaryRelation.contains(Pair(x, y))
        )
        val unmatched = inputTuple.binaryRelation.pairs
                .filter { S1.contains(it.first) && S2.contains(it.second) }
                .filter { !S.contains(it) }
        unmatched.forEach {
            val k = it.second.findPosition()
            val dstNode = inputTuple.binaryRelation.getPartner(it.second.parent!!)
            val ranges = Pair(it.first.linesRange, it.second.linesRange)
            val moveOperation = EditOperation(MOVE, it.first, dstNode, k, ranges)
            script.addAndPerform(moveOperation)
        }
    }
}