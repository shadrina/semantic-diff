package ru.nsu.diff.engine.matching

import com.intellij.psi.tree.IElementType
import com.intellij.util.text.EditDistance
import ru.nsu.diff.util.BinaryRelation
import ru.nsu.diff.util.DeltaTreeElement
import ru.nsu.diff.util.LongestCommonSubsequence
import ru.nsu.diff.util.Queue

class GoodWayMatcher(private val binaryRelation: BinaryRelation<DeltaTreeElement>) : Matcher {
    private val equalParameterT = 0.75
    private var t1Height: Int = 1
    private var t2Height: Int = 1

    override fun match(root1: DeltaTreeElement, root2: DeltaTreeElement) {
        t1Height = root1.height()
        t2Height = root2.height()

        val children1 = root1.childrenListInBfsReverseOrder().reversed()
        val children2 = root2.childrenListInBfsReverseOrder().reversed()

        preProcess(children1, children2)
        fastMatch(children1.filter { it.isLeaf() }, children2.filter { it.isLeaf() })
        fastMatch(children1.filter { !it.isLeaf() }, children2.filter { !it.isLeaf() })

        /*
            TODO: If binaryRelation contains one of roots, we need to create dummy roots for both trees
            TODO: and add them to binaryRelation
         */
        if (!binaryRelation.containsPairFor(root1) && !binaryRelation.containsPairFor(root2)) {
            binaryRelation.add(root1, root2)
        }

        postProcess(root1)
    }

    /**
     * Match nodes with the same identifier
     */
    private fun preProcess(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        val labels = mutableListOf<IElementType>()
        children1
                .filter { it.id !== null }
                .forEach { if (!labels.contains(it.label())) labels.add(it.label()) }

        for (label in labels) {
            val ids = mutableListOf<String>()

            val withLabel1 = children1.filter { it.label() == label }
            val withLabel2 = children2.filter { it.label() == label }
            withLabel1.forEach {
                if (it.id !== null && !ids.contains(it.id!!)) ids.add(it.id!!)
            }

            for (id in ids) {
                val fromT1 = withLabel1.filter { it.id == id }
                val fromT2 = withLabel2.filter { it.id == id }
                if (fromT1.size == 1 && fromT2.size == 1) {
                    binaryRelation.add(Pair(fromT1.first(), fromT2.first()))
                }
            }
        }

    }

    /**
     * @param children1 in the in-order traversal of T1 when siblings are visited left-to-right
     * @param children2 in the in-order traversal of T2 when siblings are visited left-to-right
     */
    private fun fastMatch(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        val unmatchedChildren1 = children1.filter { !binaryRelation.containsPairFor(it) }
        val unmatchedChildren2 = children2.filter { !binaryRelation.containsPairFor(it) }
        val lcs = LongestCommonSubsequence.find(unmatchedChildren1, unmatchedChildren2, this::equal)
        lcs.forEach { binaryRelation.add(it) }

        val unmatched = children1.filter { !binaryRelation.containsPairFor(it) }
        unmatched.forEach { x ->
            children2.forEach { y ->
                if (equal(x, y) && !binaryRelation.containsPairFor(y))
                    binaryRelation.add(x, y)
            }
        }
    }

    private fun postProcess(node1: DeltaTreeElement) {
        val node2 = binaryRelation.getPartner(node1) ?: return
        node1.children.forEach { child ->
            val partner = binaryRelation.getPartner(child)
            var candidate: DeltaTreeElement? = null

            if (partner === null) {
                val node2unmatchedChildren = node2.children
                        .filter { !binaryRelation.containsPairFor(it) }
                val node2matchedChildren = node2.children
                        .filter { binaryRelation.containsPairFor(it) }

                val candidateFromUnmatched = child findBestPartnerIn node2unmatchedChildren
                val candidateFromMatched = child findBestPartnerIn node2matchedChildren

                // Choose the best from candidates
                if (candidateFromUnmatched === null && candidateFromMatched !== null) {
                    val realPartnerOfCandidate = binaryRelation.getPartner(candidateFromMatched)
                    if (candidateFromMatched.betterPartnerFrom(child, realPartnerOfCandidate) === child) {
                        candidate = candidateFromMatched
                    }

                } else if (candidateFromUnmatched !== null && candidateFromMatched === null) {
                    candidate = candidateFromUnmatched

                } else if (candidateFromMatched !== null && candidateFromUnmatched !== null) {
                    val better = child.betterPartnerFrom(candidateFromUnmatched, candidateFromMatched)
                    if (better === candidateFromUnmatched) candidate = candidateFromUnmatched
                    else if (better === candidateFromMatched) {
                        val realPartnerOfCandidate = binaryRelation.getPartner(candidateFromMatched)
                        candidate = if (candidateFromMatched.betterPartnerFrom(child, realPartnerOfCandidate) === child)
                            candidateFromMatched
                        else candidateFromUnmatched
                    }
                }

            } else if (partner.parent !== node2) {
                candidate = child findBestPartnerIn node2.children

                // If on this stage there is no candidate, remove that and all
                // underlying relations which led to that relation
                if (candidate === null) child.removeRelationsInSubTree()
            }
            if (candidate !== null) {
                binaryRelation.removePairWith(child)
                binaryRelation.removePairWith(candidate)
                binaryRelation.add(child, candidate)
            }
        }
        node1.children.forEach { postProcess(it) }
    }

    private fun DeltaTreeElement.removeRelationsInSubTree() {
        binaryRelation.removePairWith(this)
        children.forEach { it.removeRelationsInSubTree() }
    }

    private infix fun DeltaTreeElement.findBestPartnerIn(nodes: List<DeltaTreeElement>) : DeltaTreeElement? {
        val boundaryPercentage = 0.7
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

    private fun DeltaTreeElement.betterPartnerFrom(node1: DeltaTreeElement?, node2: DeltaTreeElement?)
            : DeltaTreeElement? {
        if (node1 === null || node2 === null) return null

        val distance1 = EditDistance.levenshtein(text, node1.text, true)
        val distance2 = EditDistance.levenshtein(text, node2.text, true)

        return if (distance1 < distance2) node1 else node2
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
            = binaryRelation.pairs
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

    private fun DeltaTreeElement.label() = this@label.type
    private fun DeltaTreeElement.value() = this@value.text
}