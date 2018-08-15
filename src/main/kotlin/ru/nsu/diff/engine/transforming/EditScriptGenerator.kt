package ru.nsu.diff.engine.transforming

import ru.nsu.diff.engine.transforming.EditOperationType.*
import ru.nsu.diff.util.DeltaTreeElement
import ru.nsu.diff.util.InputTuple
import ru.nsu.diff.util.LongestCommonSubsequence
import ru.nsu.diff.util.Queue

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

    private fun treesAreIdentical(root1: DeltaTreeElement, root2: DeltaTreeElement)
            = root1.text.removeWhiteSpace() == root2.text.removeWhiteSpace()

    private fun String.removeWhiteSpace() = this.replace(Regex("[\r\n\t ]"), "")

    private fun bfs(queue: Queue<DeltaTreeElement>, visited: MutableList<DeltaTreeElement>) {
        while (!queue.isEmpty()) {
            val curr = queue.dequeue() ?: return
            visited.add(curr)

            val currParent = curr.parent
            var partner = inputTuple.relation.getPartner(curr)

            // Insert-phase
            if (partner === null && currParent !== null) {
                partner = curr.nodeCopy()
                inputTuple.relation.add(partner, curr)
                val insertOperation = EditOperation(
                        INSERT,
                        partner,
                        inputTuple.relation.getPartner(currParent),
                        curr.findPosition { inputTuple.relation.containsPairFor(it) },
                        Pair(null, curr.textRange)
                )
                script.addAndPerform(insertOperation)

            // Move-phase
            } else if (partner !== null && currParent !== null) {
                val partnerParent = partner.parent
                if (partnerParent != null && !inputTuple.relation.contains(Pair(partnerParent, currParent))) {
                    val moveOperation = EditOperation(
                            MOVE,
                            partner,
                            inputTuple.relation.getPartner(currParent),
                            curr.findPosition { inputTuple.relation.containsPairFor(it) },
                            Pair(partner.textRange, curr.textRange)
                    )
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
            if (!inputTuple.relation.containsPairFor(it)) {
                val ranges = Pair(it.textRange, null)
                val deleteOperation = EditOperation(DELETE, it, null, null, ranges)
                deleteOps.add(deleteOperation)
            } else it.deleteRedundant()
        }
        deleteOps.forEach {
            script.addAndPerform(it)
        }
    }

    private fun DeltaTreeElement.nodeCopy() : DeltaTreeElement {
        val newDelta = DeltaTreeElement(this.myPsi, this.type, this.text)
        newDelta.id = this.id
        newDelta.contextLevel = this.contextLevel

        return newDelta
    }

    /*
     * this@findPosition --- node from T2
     * Find rightmost sibling to the left of it
     * Return sibling's partner index
     */
    private fun DeltaTreeElement.findPosition(inOrder: (DeltaTreeElement) -> Boolean) : Int {
        val t2Parent = this.parent
        if (t2Parent === null) return 0

        val realT2idx = t2Parent.indexOf(this)
        if (realT2idx == 0) return 0

        var siblingToTheLeftIdx = realT2idx - 1
        var siblingToTheLeft = t2Parent.children[siblingToTheLeftIdx]
        while (siblingToTheLeftIdx >= 0 && !inOrder(siblingToTheLeft)) {
            siblingToTheLeft = t2Parent.children[siblingToTheLeftIdx--]
        }
        // The node in T1 before which this@findPosition should be
        val siblingPartner = inputTuple.relation.getPartner(siblingToTheLeft)
        val myPartner = inputTuple.relation.getPartner(this)

        val siblingPartnerT1idx = siblingPartner?.parent?.indexOf(siblingPartner)!!
        val realT1idx = inputTuple.relation.getPartner(t2Parent)!!.children.indexOf(myPartner)
        if (realT1idx == -1) return siblingPartnerT1idx + 1
        return siblingPartnerT1idx + if (realT1idx < siblingPartnerT1idx) 0 else 1
    }

    private fun alignChildren(elem1: DeltaTreeElement, elem2: DeltaTreeElement) {
        val S1 = elem1.children.filter { inputTuple.relation.containsPairFor(it) }
        val S2 = elem2.children.filter { inputTuple.relation.containsPairFor(it) }
        val S = LongestCommonSubsequence.find(
                S1,
                S2,
                fun (x, y) = inputTuple.relation.contains(Pair(x, y))
        )
        val unmatched = inputTuple.relation.pairs
                .filter { S1.contains(it.first) && S2.contains(it.second) }
                .filter { !S.contains(it) }
        val inOrderElements = S.toMutableList()
        unmatched.forEach { pair ->
            val moveOperation = EditOperation(
                    MOVE,
                    pair.first,
                    inputTuple.relation.getPartner(pair.second.parent!!),
                    pair.second.findPosition { elem -> inOrderElements.any { it.second === elem } },
                    Pair(pair.first.textRange, pair.second.textRange)
            )
            script.addAndPerform(moveOperation)
            inOrderElements.add(pair)
        }
    }
}