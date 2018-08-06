package ru.nsu.diff.view.util

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.ui.JBColor

import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.plaf.LayerUI

import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.util.LinesRange
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
    var currY: Int = 0

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
        if (g == null) return

        var linesRange: LinesRange?
        val lineHeight = editor.lineHeight
        val gutterWidth = editor.gutterComponentEx.width
        var gutterColor: JBColor

        val xOffset = when (side) {
            DiffSide.LEFT -> editor.component.width - gutterWidth
            DiffSide.RIGHT -> 0
        }

        for (chunk in chunks) {
            linesRange = if (side == DiffSide.LEFT) chunk.leftLines else chunk.rightLines
            gutterColor = ColorFactory.dividerOperationColor(chunk.type)

            if (linesRange === null) return
            val start = linesRange.startLine
            val stop = linesRange.stopLine

            for (i in start..stop) {
                g.color = gutterColor
                g.fillRect(xOffset, i * lineHeight - currY, gutterWidth, lineHeight)
            }
        }
    }
}