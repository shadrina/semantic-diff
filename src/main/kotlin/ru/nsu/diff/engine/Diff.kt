package ru.nsu.diff.engine

import com.intellij.psi.PsiElement
import ru.nsu.diff.engine.matching.BadWayMatcher
import ru.nsu.diff.engine.transforming.EditScript
import ru.nsu.diff.engine.transforming.EditScriptGenerator
import ru.nsu.diff.engine.util.BinaryRelation
import ru.nsu.diff.engine.util.InputTuple

object Diff {
    fun diff(root1: PsiElement, root2: PsiElement) : EditScript {
        val binaryRelation: BinaryRelation<PsiElement> = BinaryRelation<PsiElement>()
        BadWayMatcher(binaryRelation).match(root1, root2)

        return EditScriptGenerator.generateScript(InputTuple(root1, root2, binaryRelation))
    }
}