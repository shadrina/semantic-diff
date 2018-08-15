package ru.nsu.diff.engine.lang.kotlin

import ru.nsu.diff.engine.lang.LangCfg

class KotlinCfg : LangCfg() {
    override val contextManager = KotlinContextManager()
    override val uniqueInternalsNames = listOf("import_list", "package_directive")
    override val fastMatcherEqualParameter = 0.9
    override val similarityBoundaryCoefficient = 1.1
    override val strongSimilarityBoundaryPercentage = 0.9
}