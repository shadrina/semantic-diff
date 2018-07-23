package ru.nsu.diff.engine.matching

import com.intellij.psi.PsiElement
import com.intellij.util.text.EditDistance
import ru.nsu.diff.engine.util.BinaryRelation

/*
 * TODO: bad idea
 * The quality of matching depends on the constant
 */
private const val BOUNDARY_DISTANCE = 50

object BadWayMatcher : Matcher {

    /*
     * O(n^2), where n is nodes number
     * Destructive for large syntax trees (!)
     */
    override fun match(root1: PsiElement, root2: PsiElement) : BinaryRelation<PsiElement> {
        val binaryRelation = BinaryRelation<PsiElement>()
        for (child1 in root1.children) {
            var min = 100
            var pair: PsiElement? = null
            root2.children.forEach {child2 ->
                val distance = EditDistance.levenshtein(
                        child1.textRange.toString(),
                        child2.textRange.toString(),
                        false
                )
                if (distance < min) {
                    min = distance
                    pair = child2
                }
            }
            if (min < BOUNDARY_DISTANCE) binaryRelation.add(child1, pair!!)
        }
        return binaryRelation
    }
}