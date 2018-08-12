package ru.nsu.diff.engine.matching

import ru.nsu.diff.util.BinaryRelation
import ru.nsu.diff.util.DeltaTreeElement
import ru.nsu.diff.util.Queue

class MatchingManager(private val relation: BinaryRelation<DeltaTreeElement>) {

    fun match(root1: DeltaTreeElement, root2: DeltaTreeElement) {
        val children1 = root1.childrenListInBfsReverseOrder().reversed()
        val children2 = root2.childrenListInBfsReverseOrder().reversed()

        Preprocessor(relation).match(children1, children2)

        FastMatcher(relation).match(children1.filter { it.isLeaf() }, children2.filter { it.isLeaf() })
        FastMatcher(relation).match(children1.filter { !it.isLeaf() }, children2.filter { !it.isLeaf() })
        // TODO: If relation contains one of roots, we need to create dummy roots for both trees and add them to relation
        if (!relation.containsPairFor(root1) && !relation.containsPairFor(root2)) relation.add(root1, root2)

        Postprocessor(relation, root1.height()).match(root1)
    }

    private fun DeltaTreeElement.childrenListInBfsReverseOrder() : MutableList<DeltaTreeElement> {
        val queue: Queue<DeltaTreeElement> = Queue()
        queue.enqueue(this@childrenListInBfsReverseOrder)
        val visited: MutableList<DeltaTreeElement> = mutableListOf()

        while (!queue.isEmpty()) {
            val curr = queue.dequeue()!!
            visited.add(curr)

            curr.children.reversed().forEach { queue.enqueue(it) }
        }

        return visited
    }
}