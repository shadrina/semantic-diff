package ru.nsu.diff.view.util

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor

import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.plaf.LayerUI

import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.view.panels.DiffSide

/**
 * Highlights lines on gutter component of editor
 */
class GutterLayerUI(
        private val dividerPainter: DividerPainter,
        private val myComponent: JComponent,
        private val editor: EditorEx,
        private val side: DiffSide
) : LayerUI<JComponent>() {
    var chunks: List<DiffChunk> = listOf()
    private var currY: Int = 0

    init {
        editor.scrollingModel.addVisibleAreaListener(
                DiffVisibleAreaListener {
                    currY = it
                    if (side == DiffSide.LEFT) dividerPainter.currLeftY = it
                    else dividerPainter.currRightY = it

                    dividerPainter.mySplitter.repaintDivider()
                    myComponent.repaint()
                }
        )
    }

    override fun paint(g: Graphics?, c: JComponent) {
        super.paint(g, c)
        if (g === null) return

        var textRange: TextRange?
        val lineHeight = editor.lineHeight
        val gutterWidth = editor.gutterComponentEx.width
        var gutterColor: JBColor
        val paintedLines = mutableListOf<Int>()

        val xOffset = when (side) {
            DiffSide.LEFT -> editor.component.width - gutterWidth
            DiffSide.RIGHT -> 0
        }

        for (chunk in chunks) {
            textRange = if (side == DiffSide.LEFT) chunk.leftRange else chunk.rightRange
            gutterColor = ColorFactory.dividerOperationColor(chunk.type)

            if (textRange === null) continue
            val start = editor.document.getLineNumber(textRange.startOffset)
            val stop = editor.document.getLineNumber(textRange.endOffset)

            g.color = gutterColor
            (start..stop).filter { !paintedLines.contains(it) }.forEach {
                g.fillRect(xOffset, it * lineHeight - currY, gutterWidth, lineHeight)
                paintedLines.add(it)
            }
        }
    }
}