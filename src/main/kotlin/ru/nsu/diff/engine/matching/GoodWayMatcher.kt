package ru.nsu.diff.engine.matching

import com.intellij.psi.tree.IElementType
import com.intellij.util.text.EditDistance
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

class GoodWayMatcher(private val relation: BinaryRelation<DeltaTreeElement>) : Matcher {
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
            TODO: If relation contains one of roots, we need to create dummy roots for both trees
            TODO: and add them to relation
         */
        if (!relation.containsPairFor(root1) && !relation.containsPairFor(root2)) {
            relation.add(root1, root2)
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
                    relation.add(Pair(fromT1.first(), fromT2.first()))
                    // TODO: Match all their children too
                }
            }
        }

    }

    /**
     * @param children1 in the in-order traversal of T1 when siblings are visited left-to-right
     * @param children2 in the in-order traversal of T2 when siblings are visited left-to-right
     */
    private fun fastMatch(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
        val unmatchedChildren1 = children1.filter { !relation.containsPairFor(it) }
        val unmatchedChildren2 = children2.filter { !relation.containsPairFor(it) }
        val lcs = LongestCommonSubsequence.find(unmatchedChildren1, unmatchedChildren2, this::equal)
        lcs.forEach { pair ->
            if (possibleContextLevelChanges.any { it == pair.first.contextLevel() to pair.second.contextLevel() })
                relation.add(pair)
        }

        val unmatched = children1.filter { !relation.containsPairFor(it) }
        unmatched.forEach { x ->
            children2.forEach { y ->
                val from = x.contextLevel()
                val to = y.contextLevel()
                if (equal(x, y)
                        && !relation.containsPairFor(y)
                        && possibleContextLevelChanges.any { it == from to to }) relation.add(x, y)
            }
        }
    }

    /**
     * Try to find the partner for unmatched and detect future erroneous MOVEs
     */
    private fun postProcess(node1: DeltaTreeElement) {
        val node2 = relation.getPartner(node1) ?: return
        node1.children.forEach { child ->
            val partner = relation.getPartner(child)
            var candidate: DeltaTreeElement? = null

            if (partner === null) {
                val node2unmatchedChildren = node2.children
                        .filter { !relation.containsPairFor(it) }
                val node2matchedChildren = node2.children
                        .filter { relation.containsPairFor(it) }

                val candidateFromUnmatched = child findBestPartnerIn node2unmatchedChildren
                val candidateFromMatched = child findBestPartnerIn node2matchedChildren

                // Choose the best from candidates
                if (candidateFromUnmatched === null && candidateFromMatched !== null) {
                    val realPartnerOfCandidate = relation.getPartner(candidateFromMatched)
                    if (candidateFromMatched.betterPartnerFrom(child, realPartnerOfCandidate) === child) {
                        candidate = candidateFromMatched
                    }

                } else if (candidateFromUnmatched !== null && candidateFromMatched === null) {
                    candidate = candidateFromUnmatched

                } else if (candidateFromMatched !== null && candidateFromUnmatched !== null) {
                    val better = child.betterPartnerFrom(candidateFromUnmatched, candidateFromMatched)
                    if (better === candidateFromUnmatched) candidate = candidateFromUnmatched
                    else if (better === candidateFromMatched) {
                        val realPartnerOfCandidate = relation.getPartner(candidateFromMatched)
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

            } else {
                val from = child.contextProvider()
                val to = partner.contextProvider()

                if (!relation.contains(Pair(from, to))) {
                    candidate = child findBestPartnerIn node2.children
                }
            }
            if (candidate !== null) {
                relation.removePairWith(child)
                relation.removePairWith(candidate)
                relation.add(child, candidate)
            }
        }
        node1.children.forEach { postProcess(it) }
    }

    private fun DeltaTreeElement.removeRelationsInSubTree() {
        relation.removePairWith(this)
        children.forEach { it.removeRelationsInSubTree() }
    }

    private infix fun DeltaTreeElement.findBestPartnerIn(nodes: List<DeltaTreeElement>) : DeltaTreeElement? {
        // TODO: Boundary percentage should depend on the height of the subtree
        val boundaryPercentage = 1 - this.height() * 1.0 / t1Height
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
                    if (currPercentage > percentage
                            && relation.contains(Pair(this.contextProvider(), it.contextProvider()))) {
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