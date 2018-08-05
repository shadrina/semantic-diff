package ru.nsu.diff.util

data class LinesRange(var startLine: Int, var stopLine: Int) {
    fun intersectsWith(other: LinesRange?) : Boolean {
        if (other == null) return false
        val otherStart = other.startLine
        val otherStop = other.stopLine
        return otherStart.coerceIn(startLine, stopLine) == otherStart
                || otherStop.coerceIn(startLine, stopLine) == otherStop
    }

    fun merge(other: LinesRange?) {
        if (other == null) return
        val min = minOf(startLine, other.startLine)
        val max = maxOf(stopLine, other.stopLine)
        startLine = min
        stopLine = max
    }

    override fun toString(): String {
        return "($startLine, $stopLine)"
    }
}