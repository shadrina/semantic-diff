package ru.nsu.diff.view.util

import com.intellij.diff.tools.util.DiffSplitter
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.diff.impl.splitter.DividerPolygon.CTRL_PROXIMITY_X

import java.awt.Graphics
import javax.swing.JComponent
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.awt.Shape
import java.awt.geom.CubicCurve2D

import ru.nsu.diff.engine.conversion.DiffChunk

class DividerPainter(val mySplitter: DiffSplitter) : DiffSplitter.Painter {
    var leftEditor: EditorEx? = null
    var rightEditor: EditorEx? = null

    var chunks = listOf<DiffChunk>()

    var currLeftY: Int = 0
    var currRightY: Int = 0

    private fun makeCurve(width: Double, y1: Double, y2: Double, forward: Boolean): Shape {
        return if (forward) {
            CubicCurve2D.Double(.0, y1,
                    width * CTRL_PROXIMITY_X, y1,
                    width * (1.0 - CTRL_PROXIMITY_X), y2,
                    width, y2)
        } else {
            CubicCurve2D.Double(width, y2,
                    width * (1.0 - CTRL_PROXIMITY_X), y2,
                    width * CTRL_PROXIMITY_X, y1,
                    .0, y1)
        }
    }

    override fun paint(g: Graphics, component: JComponent) {
        if (leftEditor == null || rightEditor == null) return
        val lineHeight = leftEditor!!.lineHeight
        val width = component.width

        g as Graphics2D

        for (chunk in chunks) {
            if (chunk.leftLines == null || chunk.rightLines == null) continue
            val leftUpper: Double = chunk.leftLines!!.startLine * lineHeight.toDouble()
            val leftBottom: Double = (chunk.leftLines!!.stopLine + 1) * lineHeight.toDouble()
            val rightUpper: Double = chunk.rightLines!!.startLine * lineHeight.toDouble()
            val rightBottom: Double = (chunk.rightLines!!.stopLine + 1) * lineHeight.toDouble()

            val upperCurve = makeCurve(width.toDouble(), leftUpper - currLeftY, rightUpper - currRightY, true)
            val lowerCurve = makeCurve(width.toDouble(), leftBottom - currLeftY, rightBottom - currRightY, false)

            val path = Path2D.Double()
            path.append(upperCurve, true)
            path.append(lowerCurve, true)

            g.color = ColorFactory.dividerOperationColor(chunk.type)
            g.fill(path)
        }
        g.dispose()
    }
}