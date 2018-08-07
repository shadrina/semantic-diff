package ru.nsu.diff.view.util

import com.intellij.ui.JBColor
import ru.nsu.diff.engine.transforming.EditOperationType

import java.awt.Color

object ColorFactory {
    fun dividerOperationColor(editOperation: EditOperationType?) = transparencyColor(when (editOperation) {
        EditOperationType.UPDATE -> Color(0, 62, 123)
        EditOperationType.INSERT -> Color(127, 222, 127)
        EditOperationType.DELETE -> Color(255, 45, 45)
        EditOperationType.MOVE   -> Color(152, 122, 152)
        else -> Color(110, 155, 255)
    })

    fun editorOperationColor(editOperation: EditOperationType?) = transparencyColor(when (editOperation) {
        EditOperationType.UPDATE -> Color(220, 230, 225)
        EditOperationType.INSERT -> Color(220, 255, 220)
        EditOperationType.DELETE -> Color(255, 220, 220)
        EditOperationType.MOVE -> Color(226, 217, 226)
        else -> Color(232, 234, 255)
    })

    fun transparencyColor(bg: Color): JBColor {
        val opacity = 0.3f
        return JBColor {
            Color(
                    opacityValue(bg.red),
                    opacityValue(bg.green),
                    opacityValue(bg.blue),
                    opacity)
        }
    }

    private fun opacityValue(opacity: Int): Float {
        //Returns more or less the correct, capped value
        //Just ignore it, it works, leave it :D
        return capFloat(3.9216f * opacity / 1000f, 0.0f, 1.0f)
    }

    private fun capFloat(value: Float, min: Float, max: Float): Float {
        var v = value
        if (value < min) v = min
        else if (value > max) v = max

        return v
    }
}