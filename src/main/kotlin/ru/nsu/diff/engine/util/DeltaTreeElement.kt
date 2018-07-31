package ru.nsu.diff.engine.util

import com.intellij.psi.tree.IElementType

class DeltaTreeElement(
    val type: IElementType,
    var text: String
) {
    var parent: DeltaTreeElement? = null
    val children: MutableList<DeltaTreeElement> = arrayListOf()

    fun addChild(child: DeltaTreeElement, i: Int = -1) {
        if (i >= 0) children.add(i, child)
        else children.add(child)

        child.parent = this
    }

    fun removeChild(child: DeltaTreeElement) {
        children.remove(child)
    }
    fun removeChild(i: Int) {
        if (i in 0 until children.size)
        children.removeAt(i)
    }

    fun indexOf(child: DeltaTreeElement) = children.indexOf(child)

    fun isLeaf() : Boolean {
        if (children.size == 0) return true
        if (children.size == 1) return children[0].isLeaf()
        return false
    }

    fun refactorText() {
        text = ""
        children.forEach {
            text += it.text
        }
    }

    override fun toString(): String {
        return "$type"
    }
}