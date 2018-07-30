package ru.nsu.diff.engine.transforming

import com.intellij.psi.PsiElement

private const val SEPARATOR = " | "

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
            stringBuilder
                    .append(SEPARATOR)
                    .append("Type: ").append(it.type).append(SEPARATOR)
                    .append("Src: ").append(it.srcNode.name()).append(SEPARATOR)
            if (it.dstNode != null) {
                stringBuilder.append("Dst:").append(it.dstNode.name()).append(SEPARATOR)
            }
            if (it.placementIndex != null) {
                stringBuilder.append("Index:").append(it.placementIndex).append(SEPARATOR)
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    private fun PsiElement.name() = this.node.elementType.toString()
}