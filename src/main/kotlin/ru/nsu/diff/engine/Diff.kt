package ru.nsu.diff.engine

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace

import ru.nsu.diff.engine.matching.GoodWayMatcher
import ru.nsu.diff.engine.transforming.EditScript
import ru.nsu.diff.engine.transforming.EditScriptGenerator
import ru.nsu.diff.engine.util.BinaryRelation
import ru.nsu.diff.engine.util.DeltaTreeElement
import ru.nsu.diff.engine.util.InputTuple

object Diff {
    fun diff(root1: PsiElement, root2: PsiElement) : EditScript {
        val binaryRelation: BinaryRelation<DeltaTreeElement> = BinaryRelation()

        val deltaTree = buildDeltaTree(root1.node)
        val goldTree = buildDeltaTree(root2.node)
        GoodWayMatcher(binaryRelation).match(deltaTree, goldTree)

        return EditScriptGenerator.generateScript(InputTuple(deltaTree, goldTree, binaryRelation))
    }

    private fun buildDeltaTree(node: ASTNode) : DeltaTreeElement {
        val root = DeltaTreeElement(node.elementType, node.text)

        var nextChild = node.firstChildNode
        while (nextChild != null) {
            if (nextChild !is PsiWhiteSpace)
                root.addChild(buildDeltaTree(nextChild))
            nextChild = nextChild.treeNext
        }

        return root
    }
}