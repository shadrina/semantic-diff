package ru.nsu.diff.util

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

class DeltaTreeElement(
        val myPsi: PsiElement,
        val type: IElementType,
        var text: String
) {
    val name = type.toString()
    var id: String? = null
    var parent: DeltaTreeElement? = null
    val children: MutableList<DeltaTreeElement> = arrayListOf()
    val textRange: TextRange = myPsi.textRange

    /**
     * (!) Function is called only on build stage
     */
    fun identify() {
        if (name.contains("REFERENCE_EXPRESSION")
                || name.contains("DOT_QUALIFIED_EXPRESSION")
                || name.contains("CALL_EXPRESSION")) {
            id = children.firstOrNull()?.text ?: text
            return
        }
        children.forEach {
            if (it.name == "IDENTIFIER") {
                id = it.text
                return
            }
        }
    }

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

    fun nodesNumber() : Int = 1 + children.sumBy { it.nodesNumber() }

    fun height() : Int = 1 + (children.map { it.height() }.max() ?: 0)

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
        parent?.refactorText()
    }

    override fun toString(): String {
        return "$type"
    }
}