package ru.nsu.diff.engine

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import ru.nsu.diff.engine.conversion.Converter
import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.engine.lang.ContextLevel
import ru.nsu.diff.engine.lang.LangCfg

import ru.nsu.diff.engine.matching.MatchingManager
import ru.nsu.diff.engine.transforming.EditScriptGenerator
import ru.nsu.diff.util.*

class Diff(private val langCfg: LangCfg) {
    fun diff(root1: PsiElement, root2: PsiElement) : List<DiffChunk>? {
        val relation: BinaryRelation<DeltaTreeElement> = BinaryRelation()

        val deltaTree = buildDeltaTree(root1.node)
        val goldTree = buildDeltaTree(root2.node)
        deltaTree.setContext(langCfg.contextManager.initialContextLevel)
        goldTree.setContext(langCfg.contextManager.initialContextLevel)

        MatchingManager(langCfg, relation).match(deltaTree, goldTree)

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
        children.forEach { it.setContext(langCfg.contextManager.getNewContext(this, currentContextLevel)) }
    }
}