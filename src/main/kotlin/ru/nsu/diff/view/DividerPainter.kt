package ru.nsu.diff.view

import com.intellij.diff.tools.util.DiffSplitter
import com.intellij.openapi.diff.impl.splitter.DividerPolygon
import com.intellij.openapi.editor.ex.EditorEx
import ru.nsu.diff.view.util.ColorFactory
import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent
import java.awt.Graphics2D


class DividerPainter : DiffSplitter.Painter {
    var leftEditor: EditorEx? = null
    var rightEditor: EditorEx? = null

    override fun paint(g: Graphics, component: JComponent) {
        if (leftEditor == null || rightEditor == null) return
        val gg = g as Graphics2D

        val color1 = ColorFactory.transparencyColor(Color.GRAY)
        val polygon1 = DividerPolygon(10, 40, 50, 100, color1, false)
        val color2 = ColorFactory.transparencyColor(Color.GRAY)
        val polygon2 = DividerPolygon(30, 70, 80, 120, color2, false)

        val polygons = arrayListOf(polygon1, polygon2)
        DividerPolygon.paintPolygons(polygons, gg, component.width)
        gg.dispose()
    }

}