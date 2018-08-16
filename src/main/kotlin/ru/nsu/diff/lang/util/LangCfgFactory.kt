package ru.nsu.diff.lang.util

import ru.nsu.diff.lang.LangCfg
import ru.nsu.diff.lang.groovy.GroovyCfg
import ru.nsu.diff.lang.java.JavaCfg
import ru.nsu.diff.lang.kotlin.KotlinCfg

object LangCfgFactory {
    fun createLangCfg(fileExtension: String) : LangCfg? {
        return when (fileExtension) {
            "java" -> JavaCfg()
            "kt" -> KotlinCfg()
            "groovy" -> GroovyCfg()
            else -> null
        }
    }
}