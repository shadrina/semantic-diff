package ru.nsu.diff.engine.matching

import com.intellij.psi.PsiElement
import com.intellij.util.text.EditDistance
import ru.nsu.diff.engine.util.BinaryRelation

/*
     * TODO: bad idea
     * The quality of matching depends on the constant
     */
private const val BOUNDARY_DISTANCE = 60

class BadWayMatcher(private val binaryRelation: BinaryRelation<PsiElement>) : Matcher {
    /*
     * O(n^2), where n is nodes number
     * Destructive for large syntax trees (!)
     */
    override fun match(root1: PsiElement, root2: PsiElement) {
        if (root1.parent == null) {
            val rootPair = root1 findPairIn root2
            if (rootPair != null) binaryRelation.add(root1, root2)
        }
        root1.children.forEach { child1 ->
            val entryPoint = binaryRelation.getPartner(child1.parent) ?: root2
            val pair = child1 findPairIn entryPoint

            if (pair != null && !binaryRelation.containsElementWithPairOf(pair.first)) {
                binaryRelation.add(child1, pair.first)
            }
            match(child1, root2)
        }
    }

    private infix fun PsiElement.findPairIn(treeRoot: PsiElement) : Pair<PsiElement, Int>? {
        val minDistance = EditDistance.levenshtein(this.text.toString(), treeRoot.text.toString(), false)
        var result = if (treeRoot.node.elementType == this.node.elementType) Pair(treeRoot, minDistance) else null

        val candidates = treeRoot.children
                .map { this findPairIn it }
                .filter { it != null && it.first.node.elementType == this.node.elementType }
                .map { it!! }

        if (result == null && candidates.isEmpty()) return null
        if (result == null) result = candidates.first()

        candidates.forEach { if (it.second < result!!.second) result = it }
        return if (result!!.second < BOUNDARY_DISTANCE) result else null
    }
}