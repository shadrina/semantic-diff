package ru.nsu.diff.view.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener

class ScrollSynchronizer(
        var master: Editor,
        var slave: Editor
) : VisibleAreaListener {

    fun on() {
        master.scrollingModel.addVisibleAreaListener(this)
    }

    fun off() {
        master.scrollingModel.removeVisibleAreaListener(this)
    }

    fun changeRoles() {
        master.scrollingModel.removeVisibleAreaListener(this)
        val newMaster = slave
        slave = master
        master = newMaster
        master.scrollingModel.addVisibleAreaListener(this)
    }

    override fun visibleAreaChanged(e: VisibleAreaEvent?) {
        if (e === null) return
        val difference = e.newRectangle.y - e.oldRectangle.y
        val scrollingModel = slave.scrollingModel
        scrollingModel.scrollVertically(scrollingModel.verticalScrollOffset + difference)
    }
}