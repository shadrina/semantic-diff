package ru.nsu.diff.lang.kotlin

import ru.nsu.diff.lang.ContextLevel
import ru.nsu.diff.lang.ContextManager
import ru.nsu.diff.lang.*
import ru.nsu.diff.util.DeltaTreeElement

class KotlinContextManager : ContextManager() {
    override val initialContextLevel: KotlinContextLevel = TopLevel
    override val possibleContextLevelChanges = listOf(
            TopLevel to TopLevel,
            TopLevel to ClassMember,
            TopLevel to Local,
            ClassMember to ClassMember,
            ClassMember to TopLevel,
            ClassMember to Local,
            Local to Local,
            Local to Expression,
            Expression to Expression,
            Expression to Local
    )
    override val nodesIncompatibilityConditions = listOf(
            ::`srcNode is a part of an expression`,
            ::`srcNode or dstNode are primitive expressions`
    )

    override fun getNewContext(currentNode: DeltaTreeElement, currentContextLevel: ContextLevel) : ContextLevel {
        val currentNodeName = currentNode.name
        var newContextLevel = currentContextLevel
        if (currentNodeName.contains("block") || currentNodeName.contains("body")) {
            val parentName = currentNode.parent!!.name
            newContextLevel =
                    if (parentName.contains("class") || parentName.contains("object")) ClassMember
                    else if (parentName.contains("fun") && currentContextLevel != Expression) Local
                    else Expression

        } else if (currentNodeName.contains("expression")
                || currentNodeName.contains("assignment")
                || currentNodeName.contains("property")) {
            newContextLevel = Expression
        }

        return newContextLevel
    }

    private fun `srcNode is a part of an expression`(node1: DeltaTreeElement, node2: DeltaTreeElement)
            = node1.contextLevel == Expression && node1.parent?.contextLevel != Expression
    private fun `srcNode or dstNode are primitive expressions`(node1: DeltaTreeElement, node2: DeltaTreeElement)
            = node1.isLeaf() || (node2.parent?.isLeaf() ?: false)
}