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
        deltaTree.calculateRanges(root1.text.split("\r\n", "\n"))
        val goldTree = buildDeltaTree(root2.node)
        goldTree.calculateRanges(root2.text.split("\r\n", "\n"))
        GoodWayMatcher(binaryRelation).match(deltaTree, goldTree)

        val script =  EditScriptGenerator.generateScript(InputTuple(deltaTree, goldTree, binaryRelation))
        return if (script !== null) Converter.convert(script) else null
    }

    private fun buildDeltaTree(node: ASTNode) : DeltaTreeElement {
        val root = DeltaTreeElement(
                node.elementType,
                node.text)

        var nextChild = node.firstChildNode
        while (nextChild !== null) {
            if (nextChild !is PsiWhiteSpace)
                root.addChild(buildDeltaTree(nextChild))
            nextChild = nextChild.treeNext
        }

        return root
    }

    private fun DeltaTreeElement.calculateRanges(fileLines: List<String>) {
        this.calculateLinesRange(fileLines)
        children.forEach { it.calculateRanges(fileLines) }
    }
}