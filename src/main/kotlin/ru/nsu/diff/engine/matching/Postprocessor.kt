package ru.nsu.diff.engine.matching

import ru.nsu.diff.util.BinaryRelation
import ru.nsu.diff.util.ContextLevel
import ru.nsu.diff.util.DeltaTreeElement
import ru.nsu.diff.util.LongestCommonSubsequence

class Postprocessor(
        private val relation: BinaryRelation<DeltaTreeElement>,
        private val t1Height: Int
) : Matcher {
    fun match(node1: DeltaTreeElement) {
        val node2 = relation.getPartner(node1)

        if (node2 === null) {
            if (node1.`is a part of the expression`()) {
                node1.removeRelationsInSubTree()
                return
            } else return node1.children.forEach { match(it) }
        }

        for (child in node1.children) {
            val partner = relation.getPartner(child)
            var candidate: DeltaTreeElement? = null

            if (partner === null) {
                val node2unmatchedChildren = node2.children
                        .filter { !relation.containsPairFor(it) }
                val node2matchedChildren = node2.children
                        .filter { relation.containsPairFor(it) }

                val candidateFromUnmatched = child findBestPartnerIn node2unmatchedChildren
                val candidateFromMatched = child findBestPartnerIn node2matchedChildren

                if (candidateFromUnmatched !== null && candidateFromMatched === null) candidate = candidateFromUnmatched
                if (candidateFromUnmatched === null && candidateFromMatched !== null) {
                    val realPartnerOfCandidate = relation.getPartner(candidateFromMatched)
                    if (realPartnerOfCandidate!!.parent !== node1) {
                        candidate = candidateFromMatched
                    }
                }
                if (candidateFromUnmatched !== null && candidateFromMatched !== null) {
                    val realPartnerOfMatchedCandidate = relation.getPartner(candidateFromMatched)
                    candidate =
                            if (realPartnerOfMatchedCandidate!!.parent !== node1) candidateFromMatched
                            else candidateFromUnmatched
                }

            } else if (partner.parent !== node2) candidate = child findBestPartnerIn node2.children

            if (candidate !== null) {
                relation.removePairWith(child)
                relation.removePairWith(candidate)
                relation.add(child, candidate)
            }

            match(child)
        }
    }

    private fun DeltaTreeElement.`is a part of the expression`() =
            this.contextLevel == ContextLevel.EXPRESSION && this.parent?.contextLevel != ContextLevel.EXPRESSION

    private fun DeltaTreeElement.removeRelationsInSubTree() {
        relation.removePairWith(this)
        children.forEach { it.removeRelationsInSubTree() }
    }

    private infix fun DeltaTreeElement.findBestPartnerIn(nodes: List<DeltaTreeElement>) : DeltaTreeElement? {
        // TODO: Boundary percentage should depend on the height of the subtree
        val boundaryPercentage = 0.8 * (1 - this.height() * 1.0 / t1Height)
        val strongBoundaryPercentage = 0.9

        var percentage = .0
        var partner: DeltaTreeElement? = null
        nodes
                .filter { it.label() == this.label() }
                .forEach {
                    val maxLength = maxOf(this.text.length, it.text.length)
                    val lcs = LongestCommonSubsequence.find(
                            this.text.toCharArray().asList(),
                            it.text.toCharArray().asList(),
                            fun (c1: Char, c2: Char) = c1 == c2
                    )
                    val currPercentage = lcs.size * 1.0 / maxLength
                    if (currPercentage > percentage) {
                        percentage = currPercentage
                        partner = it
                    }
                }

        // If element is a leaf, require exact match
        if (this.isLeaf()) {
            return if (percentage == 1.0) partner else null
        }
        // If element has id, require either a match of the ids or a very good match of the rest
        if (this.id !== null) {
            return if (this.id == partner?.id || percentage > strongBoundaryPercentage) partner else null
        }

        return if (percentage > boundaryPercentage) partner else null
    }
}