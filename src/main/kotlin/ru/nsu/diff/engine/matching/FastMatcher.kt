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
        LOCAL to EXPRESSION,
        EXPRESSION to EXPRESSION,
        EXPRESSION to LOCAL
)

class FastMatcher(private val relation: BinaryRelation<DeltaTreeElement>) : Matcher {
    private val equalParameterT = 0.999

    fun match(nodes1: List<DeltaTreeElement>, nodes2: List<DeltaTreeElement>) {
        val nodesToMatch1 = nodes1.filter { it.isLeaf() && !relation.containsPairFor(it) }.toMutableList()
        val nodesToMatch2 = nodes2.filter { it.isLeaf() && !relation.containsPairFor(it) }.toMutableList()

        while (!nodesToMatch1.isEmpty() || !nodesToMatch2.isEmpty()) {
            val lcs = LongestCommonSubsequence.find(nodesToMatch1, nodesToMatch2, this::equal)

            val saved1 = nodesToMatch1.toList()
            val saved2 = nodesToMatch2.toList()
            nodesToMatch1.clear()
            nodesToMatch2.clear()

            lcs.forEach { relation.add(it) }

            val unmatched = saved1.filter { !relation.containsPairFor(it) }
            val matchedFromUnmatched = mutableListOf<Pair<DeltaTreeElement, DeltaTreeElement>>()
            unmatched.forEach { x ->
                val candidates = saved2.filter { y -> equal(x, y) && !relation.containsPairFor(y) }
                if (candidates.size == 1) {
                    val y = candidates.first()
                    relation.add(Pair(x, y))
                    matchedFromUnmatched.add(Pair(x, y))
                }
            }

            (lcs + matchedFromUnmatched).forEach { pair ->
                val parent1 = pair.first.parent
                val parent2 = pair.second.parent
                if (parent1 !== null && !nodesToMatch1.contains(parent1) && !relation.containsPairFor(parent1)
                        && parent1.allChildrenMatched())
                    nodesToMatch1.add(parent1)
                if (parent2 !== null && !nodesToMatch2.contains(parent2) && !relation.containsPairFor(parent2)
                        && parent2.allChildrenMatched())
                    nodesToMatch2.add(parent2)
            }
        }
    }

    private fun DeltaTreeElement.allChildrenMatched() = children.all { relation.containsPairFor(it) }

    private fun equal(x: DeltaTreeElement, y: DeltaTreeElement) : Boolean {
        if (x.isLeaf() && y.isLeaf()) {
            return x.label() == y.label() && x.value() == y.value() && contextsCompatible(x, y)
        }
        val max = maxOf(x.nodesNumber(), y.nodesNumber())
        val value = common(x, y) * 1.0 / (max - 1)
        return x.label() == y.label()
                && (value > equalParameterT || x.id !== null && x.id == y.id)
                && contextsCompatible(x, y)
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