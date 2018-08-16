package ru.nsu.diff.engine.conversion

import com.intellij.openapi.util.TextRange

import ru.nsu.diff.engine.transforming.EditOperation
import ru.nsu.diff.engine.transforming.EditOperationType

import kotlin.math.abs

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

    // TODO: refactor ugly code
    fun tryToMerge(other: EditOperation) : Boolean {
        // Create primitive chunk
        if (myOperations.isEmpty()) {
            merge(other)
            return true
        }
        // Merge MOVEs separately
        if (other.type == EditOperationType.MOVE) {
            val otherLeftRange = other.textRanges.first
            val otherRightRange = other.textRanges.second
            return if (leftRange?.intersects(otherLeftRange!!) == true
                    && rightRange?.intersects(otherRightRange!!) == true) {
                merge(other)
                true
            } else false
        } else if (myOperations.all { it.type == EditOperationType.MOVE }) return false
        // Merge recursive INSERTs
        if (myOperations.any { it.srcNode.haveParent(other.srcNode) || other.srcNode.haveParent(it.srcNode) }) {
            merge(other)
            return true
        }
        // Merge neighbour INSERTs and DELETEs
        val otherParent = other.srcNode.parent ?: return false
        if (myOperations.any {
                    val itParent = it.srcNode.parent ?: return false
                    itParent === otherParent && abs(it.placementIndex - other.placementIndex) <= 1
                }) {
            merge(other)
            return true
        }
        return false
    }

    override fun toString() : String {
        return """
            CHUNK
            Operations: ${myOperations.map { it.toString() }}
            Text ranges: ${leftRange ?: "-"}, ${rightRange ?: "-"}

        """.replaceIndent("")
    }

    private fun merge(other: EditOperation) {
        val otherLeftRange = other.textRanges.first
        val otherRightRange = other.textRanges.second
        leftRange = leftRange?.union(otherLeftRange ?: leftRange!!) ?: otherLeftRange
        rightRange = rightRange?.union(otherRightRange ?: rightRange!!) ?: otherRightRange
        myOperations.add(other)
    }
}