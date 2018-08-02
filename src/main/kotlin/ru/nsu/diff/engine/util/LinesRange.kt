package ru.nsu.diff.engine.util

data class LinesRange(val startLine: Int, val stopLine: Int) {
    override fun toString(): String {
        return "($startLine, $stopLine)"
    }
}