package ru.nsu.diff.git

import com.intellij.openapi.vfs.VirtualFile
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.EditList
import org.eclipse.jgit.diff.HistogramDiff
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter

import java.io.File
import java.io.IOException
import java.util.*

object DiffTestDataProvider {
    fun buildGitRepositoryFromVirtualFile(vf: VirtualFile) : Repository? {
        val dir = File("${vf.path.changeSeparator()}/.git")
        val repo = FileRepositoryBuilder()
                .setGitDir(dir)
                .readEnvironment()
                .findGitDir()
                .build()
        return if (dir.exists()) repo else null
    }

    fun getTestDataFromRepository(
            repository: Repository,
            extension: String,
            targetFilePath: String? = null
    ) : List<FileVersions> {
        val map = HashMap<String, MutableList<Version>>()

        val srcDir = File(repository.directory.parentFile.path)
        val fileNames = srcDir.walkTopDown()
                .filter { if (extension != "*") it.extension == extension else true }
                .map { it.path
                        .changeSeparator()
                        .removePrefix("${srcDir.path.changeSeparator()}/")
                }
                .toList()
        val targetFileName = targetFilePath
                ?.changeSeparator()
                ?.removePrefix("${srcDir.path.changeSeparator()}/")
        val filters =
                if (targetFileName == null) fileNames.map { FilterFor(PathSuffixFilter.create(it), it) }
                else listOf(FilterFor(PathSuffixFilter.create(targetFileName), targetFileName))

        val git = Git(repository)
        val log = git.log().call()
        val iterator = log.iterator()

        while (iterator.hasNext()) {
            val rev = iterator.next()
            val tree = rev.tree

            filters.forEach {
                val treeWalk = TreeWalk(repository)
                treeWalk.addTree(tree)
                treeWalk.isRecursive = true
                treeWalk.filter = it.filter

                while (treeWalk.next()) {
                    val data = repository.open(treeWalk.getObjectId(0)).bytes
                    val thatVersion = Version(rev.id.name, data)
                    val versions = map[it.fileName]
                    if (versions == null) {
                        map[it.fileName] = mutableListOf(thatVersion)
                        continue
                    }
                    val lastVersion = versions.last()
                    if (differenceCount(data, lastVersion.bytes) > 0) versions.add(thatVersion)
                }
            }
        }

        return map.map { FileVersions(it.key, it.value) }
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

    private fun String.changeSeparator() = this.replace("\\", "/")
}


