package ru.nsu.diff.engine.transforming

import com.intellij.psi.PsiElement

import ru.nsu.diff.engine.transforming.EditOperationType.*
import ru.nsu.diff.engine.util.InputTuple
import ru.nsu.diff.engine.util.LongestCommonSubsequence
import ru.nsu.diff.engine.util.Queue

object EditScriptGenerator {
    private lateinit var inputTuple: InputTuple
    private val script = EditScript()

    fun generateScript(inputTuple: InputTuple) : EditScript {
        this.inputTuple = inputTuple

        val queue = Queue<PsiElement>()
        queue.enqueue(inputTuple.T2)
        bfs(queue, mutableListOf())

        return script
    }

    private fun bfs(queue: Queue<PsiElement>, visited: MutableList<PsiElement>) {
        // while (!queue.isEmpty()) {
            val curr = queue.dequeue() ?: return
            visited.add(curr)
            curr.children.forEach { queue.enqueue(it) }

            val currParent = curr.node.treeParent?.psi
            var partner = inputTuple.binaryRelation.getPartner(curr)

            // Insert-phase
            if (partner == null && currParent != null) {
                val newNode = curr.copy()
                val dstNode = inputTuple.binaryRelation.getPartner(currParent)
                val insertOperation = EditOperation(INSERT, newNode, dstNode, curr.findPosition())
                script.addAndPerform(insertOperation)
                partner = newNode
            }
            // Move-phase
            if (partner != null && currParent != null) {
                val partnerParent = partner.node.treeParent.psi
                val moveOperation = EditOperation(MOVE, curr, partnerParent, curr.findPosition())
                script.addAndPerform(moveOperation)
            }
            // Align-phase
            alignChildren(partner!!, curr)
        // }
    }

    private fun PsiElement.findPosition() : Int = this.node.treeParent?.psi?.children?.indexOf(this) ?: 0

    private fun alignChildren(elem1: PsiElement, elem2: PsiElement) {
        val matched1 = mutableListOf<PsiElement>()
        val matched2 = mutableListOf<PsiElement>()

        val S1 = elem1.children.filter { inputTuple.binaryRelation.containsPairFor(it) }
        val S2 = elem2.children.filter { inputTuple.binaryRelation.containsPairFor(it) }
        val S = LongestCommonSubsequence.find(
                S1,
                S2,
                fun (x, y) = inputTuple.binaryRelation.contains(Pair(x, y))
        )
        S.forEach {
            matched1.add(it.first)
            matched2.add(it.second)
        }
        val unmatched = inputTuple.binaryRelation.pairs
                .filter { S1.contains(it.first) && S2.contains(it.second) }
                .filter { !S.contains(it) }
        unmatched.forEach {
            val k = it.second.findPosition()
            val moveOperation = EditOperation(MOVE, it.first, it.second.node.treeParent.psi, k)
            script.addAndPerform(moveOperation)
        }
    }
}