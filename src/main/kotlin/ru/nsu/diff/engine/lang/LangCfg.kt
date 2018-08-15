package ru.nsu.diff.engine.lang

abstract class LangCfg {
    abstract val contextManager: ContextManager
    abstract val uniqueInternalsNames: List<String>
    abstract val fastMatcherEqualParameter: Double
    abstract val similarityBoundaryCoefficient: Double
    abstract val strongSimilarityBoundaryPercentage: Double
}