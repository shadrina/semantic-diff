package ru.nsu.diff.test

import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter

import java.io.File
import java.nio.charset.Charset

import ru.nsu.diff.test.util.FileVersions

object DiffTestDataExtractor {
    fun fromGitFromRepository(path: String, extension: String) : List<FileVersions> {
        val result = mutableListOf<FileVersions>()
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return listOf()

        val fileNames = File(path.removeSuffix(".git")).listFiles()
                .filter { if (extension != "*") it.extension == extension else true }
                .map { it.name }

        val builder = FileRepositoryBuilder()
        val repository = builder
                .setGitDir(dir)
                .readEnvironment()
                .findGitDir()
                .build()
        val head = repository.getRef("refs/heads/master")

        val walk = RevWalk(repository)
        val commit = walk.parseCommit(head.objectId)
        walk.markStart(commit)

        val filters = fileNames.map { PathSuffixFilter.create(it) }

        for (i in 0 until filters.size) {
            val fileName = fileNames[i]
            val versions = mutableListOf<File>()

            for (rev in walk) {
                val tree = rev.tree
                val treeWalk = TreeWalk(repository)
                treeWalk.addTree(tree)
                treeWalk.isRecursive = true
                treeWalk.filter = filters[i]

                if (treeWalk.next()) {
                    val data = repository.open(treeWalk.getObjectId(0)).bytes
                    versions.add(File(String(data, Charset.defaultCharset())))
                }
            }
            if (!versions.isEmpty()) result.add(FileVersions(fileName, versions))
        }
        walk.dispose()

        return result
    }

    fun fromMercurialRepository(path: String, extension: String) {}
}


