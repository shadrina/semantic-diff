package ru.nsu.diff.engine.conversion

import ru.nsu.diff.engine.transforming.EditOperation
import ru.nsu.diff.engine.transforming.EditScript

data class OperationWithSelectedMark(val operation: EditOperation, var selected: Boolean)

object Converter {
    fun convert(editScript: EditScript) : List<DiffChunk> {
        val chunks = mutableListOf<DiffChunk>()
        val operationsWithSelectedMark = editScript.editOperations
                .map { OperationWithSelectedMark(it, false) }

        operationsWithSelectedMark.forEach { next ->
            if (next.selected) return@forEach

            val chunk = DiffChunk()
            chunk.add(next.operation)
            next.selected = true

            operationsWithSelectedMark.forEach {
                if (!it.selected && chunk.ableToMerge(it.operation)) {
                    chunk.add(it.operation)
                    it.selected = true
                }
            }
            chunk.initType()
            chunks.add(chunk)
        }

        return chunks
    }
}