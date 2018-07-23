package ru.nsu.diff.view

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

import java.awt.Dimension
import javax.swing.JPanel

private const val VIEWER_PANEL_WIDTH = 700
private const val VIEWER_PANEL_HEIGHT = 300
private const val ERROR_DIALOG_MSG = "Specify both files to view diff"
private const val ERROR_DIALOG_TITLE = "Oops"

class DiffViewerPanel(val project: Project) : JPanel() {
    var firstFile: VirtualFile? = null
    var secondFile: VirtualFile? = null

    init {
        preferredSize = Dimension(VIEWER_PANEL_WIDTH, VIEWER_PANEL_HEIGHT)
    }

    fun showResult() {
        if (firstFile == null || secondFile == null) {
            Messages.showMessageDialog(project, ERROR_DIALOG_MSG, ERROR_DIALOG_TITLE, Messages.getErrorIcon())
            return
        }
    }
}