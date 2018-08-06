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
        if (isPrimitive()) type = myOperations.first().type
    }

    fun ableToMerge(other: EditOperation) : Boolean {
        if (leftLines === null && rightLines === null) return true

        val otherLeftLines = other.linesRanges.first
        val otherRightLines = other.linesRanges.second
        if (leftLines === null && rightLines !== null) {
            if (otherRightLines === null && otherLeftLines !== null) {
                return rightLines!!.intersectsWith(otherLeftLines)
            }
            return rightLines!!.intersectsWith(otherRightLines)
        }
        if (rightLines === null && leftLines !== null) {
            if (otherLeftLines === null && otherRightLines !== null) {
                return leftLines!!.intersectsWith(otherRightLines)
            }
            return leftLines!!.intersectsWith(otherLeftLines)
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