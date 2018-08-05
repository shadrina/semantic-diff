package ru.nsu.diff.view.util

import java.awt.Color

object ColorFactory {
    fun transparencyColor(bg: Color): Color {
        val opacity = 0.3f
        return Color(opacityValue(bg.red), opacityValue(bg.green), opacityValue(bg.blue), opacity)
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