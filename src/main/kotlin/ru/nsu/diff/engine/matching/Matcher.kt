package ru.nsu.diff.engine.matching

import com.intellij.psi.PsiElement
import ru.nsu.diff.engine.util.BinaryRelation

interface Matcher {
    fun match(root1: PsiElement, root2: PsiElement) : BinaryRelation<PsiElement>
}