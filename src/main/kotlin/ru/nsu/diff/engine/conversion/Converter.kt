package ru.nsu.diff.engine.conversion

import ru.nsu.diff.engine.transforming.EditScript

object Converter {
    fun convert(editScript: EditScript) : List<DiffChunk> {
        val chunks = mutableListOf<DiffChunk>()
        var nextChunk = DiffChunk()

        for (operation in editScript.editOperations) {
            if (!nextChunk.ableToMerge(operation)) {
                chunks.add(nextChunk)
                if (nextChunk.myOperations.size == 1) {
                    nextChunk.type = nextChunk.myOperations.first().type
                }
                nextChunk = DiffChunk()
            }
            nextChunk.add(operation)
        }
        chunks.add(nextChunk)

        return chunks
    }
}