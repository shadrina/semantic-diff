package ru.nsu.diff.engine.matching

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import ru.nsu.diff.engine.util.BinaryRelation
import ru.nsu.diff.engine.util.LongestCommonSubsequence
import ru.nsu.diff.engine.util.Queue

class GoodWayMatcher(private val binaryRelation: BinaryRelation<PsiElement>) : Matcher {
    private val equalParameterT = 0.75

    override fun match(root1: PsiElement, root2: PsiElement) {
        val children1 = root1.childrenListInBfsReverseOrder().reversed()
        val children2 = root2.childrenListInBfsReverseOrder().reversed()

        val leafs1 = children1.filter { it.isLeaf() }
        val leafs2 = children2.filter { it.isLeaf() }
        fastMatch(leafs1, leafs2)

        val noLeafs1 = children1.filter { !it.isLeaf() }
        val noLeafs2 = children2.filter { !it.isLeaf() }
        fastMatch(noLeafs1, noLeafs2)
    }

    /**
     * @param children1 in the in-order traversal of T1 when siblings are visited left-to-right
     * @param children2 in the in-order traversal of T2 when siblings are visited left-to-right
     */
    private fun fastMatch(children1: List<PsiElement>, children2: List<PsiElement>) {
        val lcs = LongestCommonSubsequence.find(children1, children2, this::equal)
        lcs.forEach { binaryRelation.add(it) }

        val unmatched = children1.filter { !binaryRelation.containsPairFor(it) }
        unmatched.forEach { x ->
            children2.forEach { y ->
                if (equal(x, y) && !binaryRelation.containsPairFor(y))
                    binaryRelation.add(x, y)
            }
        }
    }

    private fun equal(x: PsiElement, y: PsiElement) : Boolean {
        if (x.isLeaf() && y.isLeaf()) {
            return x.label() == y.label() && x.value() == y.value()
        }
        val max = maxOf(x.nodesNumber(), y.nodesNumber())

        val value = common(x, y) * 1.0 / max
        return x.label() == y.label() && common(x, y) * 1.0 / max > equalParameterT
    }

    private fun common(x: PsiElement, y: PsiElement) : Int = binaryRelation.pairs
            .filter { it.first haveParent x && it.second haveParent y }
            .count()

    private infix fun PsiElement.haveParent(p: PsiElement) : Boolean {
        var parent = this@haveParent.psiTreeParent()
        while (parent != null) {
            if (parent === p) return true
            parent = parent.psiTreeParent()
        }
        return false
    }

    private fun PsiElement.childrenListInBfsReverseOrder() : MutableList<PsiElement> {
        val queue: Queue<PsiElement> = Queue()
        queue.enqueue(this@childrenListInBfsReverseOrder)
        val visited: MutableList<PsiElement> = mutableListOf()

        while (!queue.isEmpty()) {
            val curr = queue.dequeue()!!
            visited.add(curr)

            val currNode = curr.node
            if (currNode.firstChildNode == null) continue
            var currChildNode = currNode.firstChildNode
            val nodesToAdd = mutableListOf<ASTNode>()

            while (currChildNode != null) {
                nodesToAdd.add(currChildNode)
                currChildNode = currChildNode.treeNext
            }

            nodesToAdd.reverse()
            nodesToAdd.forEach { queue.enqueue(it.psi) }
        }

        return visited
    }

    private fun PsiElement.psiTreeParent() : PsiElement? = this@psiTreeParent.node?.treeParent?.psi

    private fun PsiElement.nodesNumber() : Int = 1 + this@nodesNumber.children.sumBy { it.nodesNumber() }

    private fun PsiElement.isLeaf() : Boolean = this@isLeaf.children.isEmpty()

    private fun PsiElement.label() : String = this@label.node.elementType.toString()

    private fun PsiElement.value() : String = this@value.text
}