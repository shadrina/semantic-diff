package ru.nsu.diff.engine.transforming

import ru.nsu.diff.engine.util.InputTuple

object EditScriptGenerator {
    private lateinit var inputTuple: InputTuple
    private val script = EditScript()

    fun generateScript(inputTuple: InputTuple) : EditScript {
        this.inputTuple = inputTuple
        updatePhase()
        movePhase()
        insertPhase()
        deletePhase()

        return script
    }

    // TODO
    private fun updatePhase() {}
    // TODO
    private fun movePhase() {}
    // TODO
    private fun insertPhase() {}
    // TODO
    private fun deletePhase() {}
}