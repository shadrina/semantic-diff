package ru.nsu.diff.engine

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import ru.nsu.diff.engine.conversion.Converter
import ru.nsu.diff.engine.conversion.DiffChunk

import ru.nsu.diff.engine.matching.GoodWayMatcher
import ru.nsu.diff.engine.transforming.EditScript
import ru.nsu.diff.engine.transforming.EditScriptGenerator
import ru.nsu.diff.util.*

object Diff {
    fun diff(root1: PsiElement, root2: PsiElement) : List<DiffChunk>? {
        val binaryRelation: BinaryRelation<DeltaTreeElement> = BinaryRelation()

        val deltaTree = buildDeltaTree(root1.node)
        val goldTree = buildDeltaTree(root2.node)
        GoodWayMatcher(binaryRelation).match(deltaTree, goldTree)

        val script =  EditScriptGenerator.generateScript(InputTuple(deltaTree, goldTree, binaryRelation))
        return if (script !== null) Converter.convert(script) else null
    }

    private fun buildDeltaTree(node: ASTNode) : DeltaTreeElement {
        val root = DeltaTreeElement(
                node.psi,
                node.elementType,
                node.text)

        var nextChild = node.firstChildNode
        while (nextChild !== null) {
            if (nextChild !is PsiWhiteSpace)
                root.addChild(buildDeltaTree(nextChild))
            nextChild = nextChild.treeNext
        }
        root.identify()
        return root
    }
}