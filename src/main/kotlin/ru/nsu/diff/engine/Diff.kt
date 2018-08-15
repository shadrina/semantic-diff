package ru.nsu.diff.engine

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import ru.nsu.diff.engine.conversion.Converter
import ru.nsu.diff.engine.conversion.DiffChunk

import ru.nsu.diff.engine.matching.MatchingManager
import ru.nsu.diff.engine.transforming.EditScriptGenerator
import ru.nsu.diff.util.*

object Diff {
    fun diff(root1: PsiElement, root2: PsiElement) : List<DiffChunk>? {
        val relation: BinaryRelation<DeltaTreeElement> = BinaryRelation()

        val deltaTree = buildDeltaTree(root1.node)
        val goldTree = buildDeltaTree(root2.node)
        deltaTree.setContext(ContextLevel.TOP_LEVEL)
        goldTree.setContext(ContextLevel.TOP_LEVEL)

        MatchingManager(relation).match(deltaTree, goldTree)

        val script =  EditScriptGenerator.generateScript(InputTuple(deltaTree, goldTree, relation))
        return if (script !== null) Converter.convert(script) else null
    }

    private fun buildDeltaTree(node: ASTNode) : DeltaTreeElement {
        val root = DeltaTreeElement(
                node.psi,
                node.elementType,
                node.text
        )

        var nextChild = node.firstChildNode
        while (nextChild !== null) {
            if (nextChild !is PsiWhiteSpace)
                root.addChild(buildDeltaTree(nextChild))
            nextChild = nextChild.treeNext
        }

        root.identify()
        return root
    }

    private fun DeltaTreeElement.setContext(currentContextLevel: ContextLevel) {
        this.contextLevel = currentContextLevel
        var newContextLevel = currentContextLevel

        if (this.name.contains("block")) {
            val parentName = this.parent!!.name
            newContextLevel =
                    if (parentName.contains("class") || parentName.contains("object")) {
                        ContextLevel.CLASS_MEMBER
                    } else if (parentName.contains("fun") && currentContextLevel != ContextLevel.EXPRESSION) {
                        ContextLevel.LOCAL
                    } else ContextLevel.EXPRESSION
        } else if (this.name.contains("expression") || this.name.contains("assignment")) {
            newContextLevel = ContextLevel.EXPRESSION
        }

        children.forEach { it.setContext(newContextLevel) }
    }
}