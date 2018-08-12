package ru.nsu.diff.engine.matching

import ru.nsu.diff.util.ContextLevel.*
import ru.nsu.diff.util.*

infix fun ContextLevel.to(that: ContextLevel) = Pair(this, that)
val possibleContextLevelChanges = listOf(
        TOP_LEVEL to TOP_LEVEL,
        TOP_LEVEL to CLASS_MEMBER,
        TOP_LEVEL to LOCAL,
        CLASS_MEMBER to CLASS_MEMBER,
        CLASS_MEMBER to TOP_LEVEL,
        CLASS_MEMBER to LOCAL,
        LOCAL to LOCAL,
        LOCAL to CLASS_MEMBER,
        EXPRESSION to EXPRESSION,
        EXPRESSION to LOCAL
)

class FastMatcher(private val relation: BinaryRelation<DeltaTreeElement>) : Matcher {
    private val equalParameterT = 0.75

    /**
     * @param children1 in the in-order traversal of T1 when siblings are visited left-to-right
     * @param children2 in the in-order traversal of T2 when siblings are visited left-to-right
     */
    fun match(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        val unmatchedChildren1 = children1.filter { !relation.containsPairFor(it) }
        val unmatchedChildren2 = children2.filter { !relation.containsPairFor(it) }
        val lcs = LongestCommonSubsequence.find(unmatchedChildren1, unmatchedChildren2, this::equal)
        lcs.forEach { pair ->
            if (contextsCompatible(pair.first, pair.second))
                relation.add(pair)
        }

        val unmatched = children1.filter { !relation.containsPairFor(it) }
        unmatched.forEach { x ->
            children2.forEach { y ->
                if (equal(x, y) && !relation.containsPairFor(y) && contextsCompatible(x, y))
                    relation.add(x, y)
            }
        }
    }

    private fun equal(x: DeltaTreeElement, y: DeltaTreeElement) : Boolean {
        if (x.isLeaf() && y.isLeaf()) {
            return x.label() == y.label() && x.value() == y.value()
        }
        val max = maxOf(x.nodesNumber(), y.nodesNumber())
        val value = common(x, y) * 1.0 / max
        return x.label() == y.label() && (value > equalParameterT || x.id !== null && x.id == y.id)
    }

    private fun common(x: DeltaTreeElement, y: DeltaTreeElement)
            = relation.pairs
            .filter { it.first haveParent x && it.second haveParent y }
            .count()

    private infix fun DeltaTreeElement.haveParent(p: DeltaTreeElement) : Boolean {
        var currParent = parent
        while (currParent != null) {
            if (currParent === p) return true
            currParent = currParent.parent
        }
        return false
    }

    private fun contextsCompatible(node1: DeltaTreeElement, node2: DeltaTreeElement) =
            possibleContextLevelChanges.contains(node1.contextLevel to node2.contextLevel)
}