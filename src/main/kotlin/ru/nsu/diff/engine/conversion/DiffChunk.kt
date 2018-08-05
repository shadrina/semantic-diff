package ru.nsu.diff.engine.conversion

import ru.nsu.diff.engine.transforming.EditOperation
import ru.nsu.diff.util.LinesRange

class DiffChunk {
    var leftLines: LinesRange? = null
    var rightLines: LinesRange? = null

    val myOperations = mutableListOf<EditOperation>()

    fun ableToMerge(other: EditOperation) : Boolean {
        if (leftLines == null || rightLines == null) return false
        val otherLeftLines = other.linesRanges.first
        val otherRightLines = other.linesRanges.second
        return leftLines!!.intersectsWith(otherLeftLines) || rightLines!!.intersectsWith(otherRightLines)
    }

    fun add(other: EditOperation) {
        val otherLeftLines = other.linesRanges.first
        val otherRightLines = other.linesRanges.second
        if (leftLines == null || rightLines == null) {
            leftLines = otherLeftLines
            rightLines = otherRightLines
        } else if (ableToMerge(other)) {
            leftLines!!.merge(otherLeftLines)
            rightLines!!.merge(otherRightLines)
        }
        myOperations.add(other)

    }
}