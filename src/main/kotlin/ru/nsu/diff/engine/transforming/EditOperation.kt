package ru.nsu.diff.engine.transforming

import com.intellij.openapi.util.TextRange
import ru.nsu.diff.util.DeltaTreeElement

enum class EditOperationType {
    UPDATE, // unused type
    MOVE,
    INSERT,
    DELETE
}

data class EditOperation (
        val type: EditOperationType,
        val srcNode: DeltaTreeElement,
        val dstNode: DeltaTreeElement?,
        val placementIndex: Int,
        val textRanges: Pair<TextRange?, TextRange?>
) {
    override fun toString() : String =
        when (type) {
            EditOperationType.UPDATE -> ""
            EditOperationType.MOVE -> {
                """
                    Moved ${srcNode.name} ${if (srcNode.id !== null) "\"${srcNode.id}\" " else ""}
                    in ${dstNode!!.name} ${if (dstNode.id !== null) "\"${dstNode.id}\" " else ""}
                """.trimIndent().replace(Regex("[\r\n\t]"), "")
            }
            EditOperationType.INSERT -> {
                """
                    Inserted ${srcNode.name} ${if (srcNode.id !== null) "\"${srcNode.id}\" " else ""}
                    in ${dstNode!!.name} ${if (dstNode.id !== null) "\"${dstNode.id}\" " else ""}
                """.trimIndent().replace(Regex("[\r\n\t]"), "")
            }
            EditOperationType.DELETE -> {
                "Deleted ${srcNode.name} ${if (srcNode.id !== null) "\"${srcNode.id}\" " else ""}"
            }
        }

    fun isValid() : Boolean =
        when (type) {
            EditOperationType.UPDATE -> true
            EditOperationType.MOVE   -> dstNode !== null
            EditOperationType.INSERT -> dstNode !== null
            EditOperationType.DELETE -> true
        }

    fun perform() {
        if (!isValid()) return
        when (type) {
            EditOperationType.UPDATE -> {}
            EditOperationType.MOVE -> {
                srcNode.parent?.removeChild(srcNode)
                dstNode!!.addChild(srcNode, placementIndex)
                dstNode.refactorText()
            }
            EditOperationType.INSERT -> {
                dstNode!!.addChild(srcNode, placementIndex)
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