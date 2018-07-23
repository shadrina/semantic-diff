package ru.nsu.diff.engine.util

import com.intellij.psi.PsiElement

data class InputTuple(val T1: PsiElement, val T2: PsiElement, val binaryRelation: BinaryRelation<PsiElement>) {}