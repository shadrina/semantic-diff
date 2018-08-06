package ru.nsu.diff.view.util

import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener

class DiffVisibleAreaListener(
        private val after: (Int) -> Unit
) : VisibleAreaListener {
    override fun visibleAreaChanged(e: VisibleAreaEvent?) {
        after(e?.newRectangle?.y ?: 0)
    }
}