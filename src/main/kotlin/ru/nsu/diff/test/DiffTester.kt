package ru.nsu.diff.test

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.diff.Diff
import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.engine.transforming.EditOperation

import ru.nsu.diff.engine.transforming.EditScript

object DiffTester {
    private var passed = 0
    private var failed = 0

    fun test(file1: VirtualFile, file2: VirtualFile, chunks: List<DiffChunk>) {
        val text1 = file1.inputStream.bufferedReader().readText()
        val text2 = file2.inputStream.bufferedReader().readText()
        val diffChanges = Diff.buildChanges(text1, text2)

        if (diffChanges != null) test(chunks, diffChanges.toList())
        // TODO: else
    }

    fun test(semDiffChanges: List<DiffChunk>, diffChanges: List<Diff.Change>) {
        val semDiffChangesCount = semDiffChanges.size
        val diffChangesCount =  diffChanges.size

        diffChanges.forEach {
            println(it)
        }

        if (semDiffChangesCount <= diffChangesCount) passed++
        else failed++
    }

    override fun toString(): String {
        return "Passed: $passed | Failed: $failed"
    }
}