package ru.nsu.diff.util

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

class DeltaTreeElement(
        val myPsi: PsiElement,
        val type: IElementType,
        var text: String
) {
    lateinit var linesRange: LinesRange
    val name = type.toString()
    var id: String? = null
    var parent: DeltaTreeElement? = null
    val children: MutableList<DeltaTreeElement> = arrayListOf()

    /**
     * (!) Function is called only on build stage
     */
    fun calculateLinesRange(fileLines: List<String>) {
        val myIndex = parent?.indexOf(this) ?: 0
        val leftSibling = parent?.children?.getOrNull(myIndex - 1)
        val leftSiblingLines = leftSibling?.text?.split("\r\n", "\n")

        val parentStartLine = parent?.linesRange?.startLine ?: 0
        val parentStopLine =  parent?.linesRange?.stopLine ?: fileLines.lastIndex
        val linesToSearch: MutableList<String> = fileLines.subList(parentStartLine, parentStopLine + 1).toMutableList()

        var parentOffset = 0
        for (i in 0 until (leftSiblingLines?.size ?: -1)) {
            if (leftSiblingLines!![i] == linesToSearch.first()) {
                linesToSearch.removeAt(0)
                parentOffset++
            }
        }

        val myLines = text.split("\r\n", "\n")
        val startOffset = parentStartLine + parentOffset
        val startLine = startOffset + linesToSearch.indexOfFirst { it.contains(myLines[0]) }
        val stopLine = startLine + myLines.size - 1
        linesRange = LinesRange(startLine, stopLine)
    }
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
            if (it.name.contains("IDENTIFIER")) {
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

    fun psiRange() = myPsi.textRange

    override fun toString(): String {
        return "$type"
    }
}