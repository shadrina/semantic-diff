package ru.nsu.diff.test

import org.eclipse.jgit.diff.EditList
import org.eclipse.jgit.diff.HistogramDiff
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter

import java.io.File
import java.io.IOException

import ru.nsu.diff.test.util.FileVersions

object DiffTestDataExtractor {
    fun getTestDataFromRepository(path: String, extension: String) : List<FileVersions> {
        val result = mutableListOf<FileVersions>()
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return listOf()

        val srcDir = File(path.removeSuffix(".git"))
        val fileNames = srcDir.walkTopDown()
                .filter { if (extension != "*") it.extension == extension else true }
                .map { it.path.removePrefix("${srcDir.path}\\").replace("\\", "/") }
                .toList()

        val builder = FileRepositoryBuilder()
        val repository = builder
                .setGitDir(dir)
                .readEnvironment()
                .findGitDir()
                .build()
        val head = repository.getRef("refs/heads/master")

        val filters = fileNames.map { PathSuffixFilter.create(it) }

        for (i in 0 until filters.size) {
            val walk = RevWalk(repository)
            val commit = walk.parseCommit(head.objectId)
            walk.markStart(commit)

            val fileName = fileNames[i]
            val versions = mutableListOf<ByteArray>()

            for (rev in walk) {
                val tree = rev.tree
                val treeWalk = TreeWalk(repository)
                treeWalk.addTree(tree)
                treeWalk.isRecursive = true
                treeWalk.filter = filters[i]

                if (treeWalk.next()) {
                    val data = repository.open(treeWalk.getObjectId(0)).bytes
                    val lastData = versions.lastOrNull()
                    if (lastData == null || differenceCount(data, lastData) > 0) versions.add(data)
                }
            }
            if (versions.size > 1) result.add(FileVersions(fileName, versions))
            walk.dispose()
        }

        return result
    }

    private fun differenceCount(bytes1: ByteArray, bytes2: ByteArray) : Int {
        var count = 0
        try {
            val rt1 = RawText(bytes1)
            val rt2 = RawText(bytes2)
            val diffList = EditList()
            diffList.addAll(HistogramDiff().diff(RawTextComparator.DEFAULT, rt1, rt2))
            count = diffList.size
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return count
    }
}


