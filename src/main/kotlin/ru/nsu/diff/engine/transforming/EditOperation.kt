package ru.nsu.diff.engine.transforming

import ru.nsu.diff.engine.util.DeltaTreeElement

private const val SEPARATOR = " | "

enum class EditOperationType {
    UPDATE, // unused type
    MOVE,
    INSERT,
    DELETE
}

class EditOperation (
        val type: EditOperationType,
        val srcNode: DeltaTreeElement,
        val dstNode: DeltaTreeElement?,
        val placementIndex: Int?
) {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder
                .append("Type: ").append(type)
                .append(SEPARATOR)
                .append("Src: ").append(srcNode.name())
                .append(SEPARATOR)
        if (dstNode != null && placementIndex != null) {
            stringBuilder
                    .append("Dst: ").append(dstNode.name())
                    .append(SEPARATOR)
                    .append("Index: ").append(placementIndex)
        }
        return stringBuilder.toString()
    }
    private fun DeltaTreeElement.name() = this.type.toString()

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
            EditOperationType.UPDATE -> {}
            EditOperationType.MOVE -> {
                srcNode.parent?.removeChild(srcNode)
                dstNode!!.addChild(srcNode, placementIndex!!)
                dstNode.refactorText()
            }
            EditOperationType.INSERT -> {
                dstNode!!.addChild(srcNode, placementIndex!!)
                dstNode.refactorText()
            }
            EditOperationType.DELETE -> {
                srcNode.parent?.removeChild(srcNode)
            }
        }
    }
}