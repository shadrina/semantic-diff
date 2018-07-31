package ru.nsu.diff.engine.matching

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

import ru.nsu.diff.engine.util.BinaryRelation
import ru.nsu.diff.engine.util.DeltaTreeElement
import ru.nsu.diff.engine.util.LongestCommonSubsequence
import ru.nsu.diff.engine.util.Queue

class GoodWayMatcher(private val binaryRelation: BinaryRelation<DeltaTreeElement>) : Matcher {
    private val equalParameterT = 0.51

    override fun match(root1: DeltaTreeElement, root2: DeltaTreeElement) {
        val children1 = root1.childrenListInBfsReverseOrder().reversed()
        val children2 = root2.childrenListInBfsReverseOrder().reversed()

        fastMatch(children1.filter { it.isLeaf() }, children2.filter { it.isLeaf() })
        fastMatch(children1.filter { !it.isLeaf() }, children2.filter { !it.isLeaf() })
    }

    /**
     * @param children1 in the in-order traversal of T1 when siblings are visited left-to-right
     * @param children2 in the in-order traversal of T2 when siblings are visited left-to-right
     */
    private fun fastMatch(children1: List<DeltaTreeElement>, children2: List<DeltaTreeElement>) {
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

    private fun equal(x: DeltaTreeElement, y: DeltaTreeElement) : Boolean {
        if (x.isLeaf() && y.isLeaf()) {
            return x.label() == y.label() && x.value() == y.value()
        }
        val max = maxOf(x.nodesNumber(), y.nodesNumber())
        val value = common(x, y) * 1.0 / max
        return x.label() == y.label() && common(x, y) * 1.0 / max > equalParameterT
    }

    private fun common(x: DeltaTreeElement, y: DeltaTreeElement) : Int = binaryRelation.pairs
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

    private fun DeltaTreeElement.nodesNumber() : Int = 1 + this@nodesNumber.children.sumBy { it.nodesNumber() }

    private fun DeltaTreeElement.label() = this@label.type

    private fun DeltaTreeElement.value() = this@value.text
}