package ru.nsu.diff.view.util

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor

import ru.nsu.diff.engine.transforming.EditOperationType
import ru.nsu.diff.engine.transforming.EditOperationType.*

import java.awt.Color

object ColorFactory {
    fun selectionColor(background: JBColor) : JBColor {
        val scheme = EditorColorsManager.getInstance().globalScheme
        val offset = if (scheme.name.contains("Darcula")) 10 else -12
        return JBColor {
            Color(
                    background.red + offset,
                    background.green + offset,
                    background.blue + offset
            )
        }
    }

    fun dividerOperationColor(editOperation: EditOperationType?) : JBColor
            = transparencyColor(when (editOperation) {
        UPDATE -> Color(0, 62, 123)
        INSERT -> Color(127, 222, 127)
        DELETE -> Color(255, 45, 45)
        MOVE   -> Color(152, 122, 152)
        else -> Color(110, 155, 255)
    })

    // TODO: There can be many dark color schemes
    fun editorOperationColor(editOperation: EditOperationType?) : JBColor {
        val scheme = EditorColorsManager.getInstance().globalScheme
        return transparencyColor(
                if (scheme.name.contains("Darcula")) darkEditorOperationColor(editOperation)
                else defaultEditorOperationColor(editOperation)
        )
    }

    private fun defaultEditorOperationColor(editOperation: EditOperationType?) : Color
            = when (editOperation) {
        UPDATE -> Color(220, 230, 225)
        INSERT -> Color(220, 255, 220)
        DELETE -> Color(255, 220, 220)
        MOVE -> Color(226, 217, 226)
        else -> Color(232, 234, 255)
    }

    private fun darkEditorOperationColor(editOperation: EditOperationType?) : Color
            = when (editOperation) {
        UPDATE -> Color(75, 91, 122)
        INSERT -> Color(94, 128, 97)
        DELETE -> Color(137, 56, 58)
        MOVE -> Color(88, 81, 91)
        else -> Color(75, 91, 122)
    }

    private fun transparencyColor(bg: Color): JBColor {
        val opacity = 0.3f
        return JBColor {
            Color(
                    opacityValue(bg.red),
                    opacityValue(bg.green),
                    opacityValue(bg.blue),
                    opacity)
        }
    }

    // TODO: Looks strange
    private fun opacityValue(opacity: Int) : Float
            = capFloat(3.9216f * opacity / 1000f, 0.0f, 1.0f)

    private fun capFloat(value: Float, min: Float, max: Float) : Float
            = when {
        value < min -> min
        value > max -> max
        else -> value
    }
}