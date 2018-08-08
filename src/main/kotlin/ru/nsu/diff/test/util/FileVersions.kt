package ru.nsu.diff.test.util

data class Version(val hash: String, val bytes: ByteArray)

data class FileVersions(val fileName: String, val versions: List<Version>)