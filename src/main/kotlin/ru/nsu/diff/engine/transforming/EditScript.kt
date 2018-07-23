package ru.nsu.diff.engine.transforming

private const val SEPARATOR = " "

data class EditScript(val editOperations: MutableList<EditOperation> = mutableListOf()) {

    fun addOperation(operation: EditOperation) {
        if (operation.isValid()) editOperations.add(operation)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        editOperations.forEach {
            stringBuilder
                    .append("Type: ").append(it.type).append(SEPARATOR)
                    .append("Src: ").append(it.srcNode.text).append(SEPARATOR)
            if (it.dstNode != null) {
                stringBuilder.append("Dst:").append(it.dstNode.text).append(SEPARATOR)
            }
            if (it.placementIndex != null) {
                stringBuilder.append("Index:").append(it.placementIndex).append(SEPARATOR)
            }
        }
        return stringBuilder.toString()
    }
}