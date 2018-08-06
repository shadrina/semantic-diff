package ru.nsu.diff.view.util

import com.intellij.diff.tools.util.DiffSplitter
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.diff.impl.splitter.DividerPolygon.CTRL_PROXIMITY_X
import com.intellij.ui.JBColor

import java.awt.Graphics
import javax.swing.JComponent
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.awt.Shape
import java.awt.geom.CubicCurve2D

import ru.nsu.diff.engine.conversion.DiffChunk

class DividerPainter : DiffSplitter.Painter, VisibleAreaListener {
    var leftEditor: EditorEx? = null
        set(value) {
            value?.scrollingModel?.addVisibleAreaListener(this)
            field = value
        }
    var rightEditor: EditorEx? = null
        set(value) {
            value?.scrollingModel?.addVisibleAreaListener(this)
            field = value
        }
    private val polygons = mutableListOf<Pair<Path2D, JBColor>>()

    fun createPolygons(chunks: List<DiffChunk>, width: Int) {
        if (leftEditor == null || rightEditor == null) return
        val lineHeight = leftEditor!!.lineHeight
        for (chunk in chunks) {
            var leftUpper: Double; var leftBottom: Double; var rightUpper: Double; var rightBottom: Double

            // inserts and deletes paint later
            if (chunk.leftLines == null || chunk.rightLines == null) {
                // TODO: calculate
                continue
            }

            leftUpper = chunk.leftLines!!.startLine * lineHeight.toDouble()
            leftBottom = (chunk.leftLines!!.stopLine + 1) * lineHeight.toDouble()

            rightUpper = chunk.rightLines!!.startLine * lineHeight.toDouble()
            rightBottom = (chunk.rightLines!!.stopLine + 1) * lineHeight.toDouble()

            val upperCurve = makeCurve(width.toDouble(), leftUpper, rightUpper, true)
            val lowerCurve = makeCurve(width.toDouble(), leftBottom, rightBottom, false)
            val path = Path2D.Double()
            path.append(upperCurve, true)
            path.append(lowerCurve, true)

            polygons.add(Pair(path, ColorFactory.dividerOperationColor(chunk.type)))
        }
    }

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

    override fun visibleAreaChanged(e: VisibleAreaEvent?) {

    }

    override fun paint(g: Graphics, component: JComponent) {
        if (leftEditor == null || rightEditor == null) return
        val gg = g as Graphics2D

        for (polygon in polygons) {
            val path = polygon.first
            val color = polygon.second
            gg.color = color
            gg.fill(path)
        }

        gg.dispose()
    }
}