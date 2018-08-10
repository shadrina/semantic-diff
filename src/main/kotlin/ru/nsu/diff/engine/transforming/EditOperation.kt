package ru.nsu.diff.engine.transforming

import com.intellij.openapi.util.TextRange
import ru.nsu.diff.util.DeltaTreeElement

private const val SEPARATOR = " | "

enum class EditOperationType {
    UPDATE, // unused type
    MOVE,
    INSERT,
    DELETE
}

class EditOperation (
        val type: EditOperationType,
        private val srcNode: DeltaTreeElement,
        private val dstNode: DeltaTreeElement?,
        private val placementIndex: Int?,
        val textRanges: Pair<TextRange?, TextRange?>
) {
    override fun toString() : String {
        val stringBuilder = StringBuilder()
        stringBuilder
                .append("Type: ").append(type)
                .append(SEPARATOR)
                .append("Src: ").append(srcNode.name())
        if (dstNode != null && placementIndex != null) {
            stringBuilder
                    .append(SEPARATOR)
                    .append("Dst: ").append(dstNode.name())
                    .append(SEPARATOR)
                    .append("Index: ").append(placementIndex)
        }
        stringBuilder.append(SEPARATOR).append("Lines: $textRanges")

        return stringBuilder.toString()
    }
    private fun DeltaTreeElement.name() = this.type.toString()

    fun toShortString() : String {
        return "Type: $type, Lines: $textRanges"

    }

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
                val parent = srcNode.parent
                parent?.removeChild(srcNode)
                parent?.refactorText()
            }
        }
    }
}