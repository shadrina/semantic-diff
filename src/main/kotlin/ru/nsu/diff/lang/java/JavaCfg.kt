package ru.nsu.diff.lang.java

import ru.nsu.diff.lang.LangCfg

class JavaCfg : LangCfg() {
    override val contextManager = JavaContextManager()
    override val uniqueInternalsNames: List<String>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val fastMatcherEqualParameter: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val similarityBoundaryCoefficient: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val strongSimilarityBoundaryPercentage: Double
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}