package ru.nsu.diff.engine.lang.groovy

import ru.nsu.diff.engine.lang.ContextLevel
import ru.nsu.diff.engine.lang.ContextLevelChange
import ru.nsu.diff.engine.lang.ContextManager
import ru.nsu.diff.util.DeltaTreeElement

class GroovyContextManager : ContextManager() {
    override val initialContextLevel: ContextLevel
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val possibleContextLevelChanges: List<ContextLevelChange>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val nodesIncompatibilityConditions: List<(DeltaTreeElement, DeltaTreeElement) -> Boolean>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun getNewContext(currentNode: DeltaTreeElement, currentContextLevel: ContextLevel): ContextLevel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}