package ru.nsu.diff.view

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.ui.JBColor

import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.plaf.LayerUI

import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.util.LinesRange
import ru.nsu.diff.view.util.ColorFactory

class GutterLayerUI(
        private val editor: EditorEx,
        private val side: DiffSide
) : LayerUI<JComponent>() {
    var chunks: List<DiffChunk> = listOf()

    override fun paint(g: Graphics?, c: JComponent) {
        super.paint(g, c)
        if (g == null) return

        var linesRange: LinesRange?
        val lineHeight = editor.lineHeight
        val gutterWidth = editor.gutterComponentEx.width
        var gutterColor: JBColor

        val xOffset = when (side) {
            DiffSide.LEFT  -> editor.component.width - gutterWidth
            DiffSide.RIGHT -> 0
        }

        for (chunk in chunks) {
            linesRange = if (side == DiffSide.LEFT) chunk.leftLines else chunk.rightLines
            gutterColor = ColorFactory.dividerOperationColor(chunk.type)
            val start = linesRange?.startLine ?: 0
            val stop = linesRange?.stopLine ?: 0

            for (i in start..stop) {
                g.color = gutterColor
                g.fillRect(xOffset, i * lineHeight, gutterWidth, lineHeight)
            }
        }
    }
}