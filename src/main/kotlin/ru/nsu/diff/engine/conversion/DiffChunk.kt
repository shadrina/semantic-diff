package ru.nsu.diff.engine.conversion

import ru.nsu.diff.engine.transforming.EditOperation
import ru.nsu.diff.engine.transforming.EditOperationType
import ru.nsu.diff.util.LinesRange

class DiffChunk {
    var leftLines: LinesRange? = null
    var rightLines: LinesRange? = null

    var type: EditOperationType? = null
    private val myOperations = mutableListOf<EditOperation>()

    fun initType() {
        val currType = myOperations.firstOrNull()?.type ?: return
        myOperations.forEach {
            if (it.type != currType) return
        }
        type = currType
    }

    fun ableToMerge(other: EditOperation) : Boolean {
        if (leftLines === null && rightLines === null) return true

        val otherLeftLines = other.linesRanges.first
        val otherRightLines = other.linesRanges.second
        if (leftLines === null && rightLines !== null) {
            return if (otherRightLines === null && otherLeftLines !== null) rightLines!!.intersectsWith(otherLeftLines)
            else rightLines!!.intersectsWith(otherRightLines)
        }
        if (rightLines === null && leftLines !== null) {
            return if (otherLeftLines === null && otherRightLines !== null) leftLines!!.intersectsWith(otherRightLines)
            else leftLines!!.intersectsWith(otherLeftLines)
        }
        return leftLines!!.intersectsWith(otherLeftLines) && rightLines!!.intersectsWith(otherRightLines)
    }

    fun add(other: EditOperation) {
        val otherLeftLines = other.linesRanges.first
        val otherRightLines = other.linesRanges.second
        if (leftLines == null) leftLines = otherLeftLines
        else leftLines!!.merge(otherLeftLines)
        if (rightLines == null) rightLines = otherRightLines
        else rightLines!!.merge(otherRightLines)

        myOperations.add(other)
    }

    override fun toString(): String {
        return """
            CHUNK
            Operations: ${myOperations.map { it.toShortString() }}
            Line ranges: ${leftLines ?: "-"}, ${rightLines ?: "-"}

        """.replaceIndent("")
    }

    private fun isPrimitive() = myOperations.size == 1
}