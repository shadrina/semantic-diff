package ru.nsu.diff.engine.transforming

import com.intellij.psi.PsiElement

enum class EditOperationType {
    UPDATE, // unused type
    MOVE,
    INSERT,
    DELETE
}

class EditOperation(
        val type: EditOperationType,
        val srcNode: PsiElement,
        val dstNode: PsiElement?,
        val placementIndex: Int?
) {
    fun isValid() : Boolean =
        when (type) {
            EditOperationType.UPDATE -> true
            EditOperationType.MOVE   -> dstNode != null && placementIndex != null
            EditOperationType.INSERT -> dstNode != null && placementIndex != null
            EditOperationType.DELETE -> true
        }

    fun perform() {
        if (!isValid()) return
        when (type) {
            EditOperationType.UPDATE -> {
            }
            EditOperationType.MOVE -> {
                dstNode!!.addBefore(srcNode, dstNode.children[placementIndex!!])

                val srcNodePlacementIndex = srcNode.parent.children.indexOf(srcNode)
                srcNode.parent.children.drop(srcNodePlacementIndex)
            }
            EditOperationType.INSERT -> {
                dstNode!!.addBefore(srcNode, dstNode.children[placementIndex!!])
            }
            EditOperationType.DELETE -> {
                val srcNodePlacementIndex = srcNode.parent.children.indexOf(srcNode)
                srcNode.parent.children.drop(srcNodePlacementIndex)
            }
        }
    }
}