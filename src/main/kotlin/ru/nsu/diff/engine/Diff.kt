package ru.nsu.diff.engine

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import ru.nsu.diff.engine.conversion.Converter
import ru.nsu.diff.engine.conversion.DiffChunk

import ru.nsu.diff.engine.matching.GoodWayMatcher
import ru.nsu.diff.engine.transforming.EditScriptGenerator
import ru.nsu.diff.util.*

object Diff {
    fun diff(root1: PsiElement, root2: PsiElement) : List<DiffChunk>? {
        val relation: BinaryRelation<DeltaTreeElement> = BinaryRelation()

        val deltaTree = buildDeltaTree(root1.node)
        val goldTree = buildDeltaTree(root2.node)
        deltaTree.setContext(mutableListOf(ContextInfo(ContextLevel.TOP_LEVEL, deltaTree)))
        goldTree.setContext(mutableListOf(ContextInfo(ContextLevel.TOP_LEVEL, deltaTree)))

        GoodWayMatcher(relation).match(deltaTree, goldTree)

        val script =  EditScriptGenerator.generateScript(InputTuple(deltaTree, goldTree, relation))
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

    private fun DeltaTreeElement.setContext(currentContext: List<ContextInfo>) {
        this.contextStack = currentContext

        var contextLevel = currentContext.lastOrNull()?.contextLevel
        val contextProvider = this

        if (this.name.contains("class")) contextLevel = ContextLevel.CLASS_MEMBER
        if (this.name.contains("fun"))   contextLevel = ContextLevel.LOCAL
        if (this.name.contains("expression") || this.name.contains("assignment"))
            contextLevel = ContextLevel.EXPRESSION

        val newContext = currentContext.toMutableList()
        if (contextLevel != currentContext.lastOrNull()?.contextLevel) {
            newContext.add(ContextInfo(contextLevel!!, contextProvider))
        }

        children.forEach { it.setContext(newContext) }
    }
}