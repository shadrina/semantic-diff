package ru.nsu.diff.engine.transforming

data class EditScript(val editOperations: MutableList<EditOperation> = mutableListOf()) {

    fun addOperation(operation: EditOperation) {
        if (operation.isValid()) editOperations.add(operation)
    }

    fun addAndPerform(operation: EditOperation) {
        if (operation.isValid()) editOperations.add(operation)
        operation.perform()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        editOperations.forEach {
            stringBuilder.append(it).append("\n")
        }
        return stringBuilder.toString()
    }
}