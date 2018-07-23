package ru.nsu.diff.engine

import com.intellij.psi.PsiElement

class Comparator {
    companion object {
        fun compare(root1: PsiElement, root2: PsiElement) : DiffResult {
            return DiffResult()
        }
    }
}