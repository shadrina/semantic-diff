package ru.nsu.diff.engine.conversion

import com.intellij.openapi.util.TextRange
import ru.nsu.diff.engine.transforming.EditOperation
import ru.nsu.diff.engine.transforming.EditOperationType

class DiffChunk {
    var leftRange: TextRange? = null
    var rightRange: TextRange? = null

    var type: EditOperationType? = null
    private val myOperations = mutableListOf<EditOperation>()

    fun initType() {
        val currType = myOperations.firstOrNull()?.type ?: return
        myOperations.forEach {
            if (it.type != currType) return
        }
        type = currType
    }

    // TODO: works wrong
    fun ableToMerge(other: EditOperation) : Boolean {
        val otherLeftRange = other.textRanges.first
        val otherRightRange = other.textRanges.second

        return leftRange?.intersects(otherLeftRange ?: TextRange.EMPTY_RANGE) ?: true
                && rightRange?.intersects(otherRightRange ?: TextRange.EMPTY_RANGE) ?: true
    }

    fun add(other: EditOperation) {
        val otherLeftRange = other.textRanges.first
        val otherRightRange = other.textRanges.second
        if (leftRange == null) leftRange = otherLeftRange
        else leftRange!!.union(otherLeftRange ?: leftRange!!)
        if (rightRange == null) rightRange = otherRightRange
        else rightRange!!.union(otherRightRange ?: rightRange!!)

        myOperations.add(other)
    }

    override fun toString(): String {
        return """
            CHUNK
            Operations: ${myOperations.map { it.toString() }}
            Text ranges: ${leftRange ?: "-"}, ${rightRange ?: "-"}

        """.replaceIndent("")
    }
}