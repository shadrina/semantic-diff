package ru.nsu.diff.vcs.git

import org.eclipse.jgit.treewalk.filter.PathSuffixFilter
import java.util.*

data class Version(val hash: String, val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Version

        if (hash != other.hash) return false
        if (!Arrays.equals(bytes, other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hash.hashCode()
        result = 31 * result + Arrays.hashCode(bytes)
        return result
    }
}

data class FileVersions(val fileName: String, val versions: List<Version>)

data class FilterFor(val filter: PathSuffixFilter, val fileName: String)