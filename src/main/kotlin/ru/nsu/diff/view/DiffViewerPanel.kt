package ru.nsu.diff.view

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager

import java.awt.Dimension
import javax.swing.JPanel

import ru.nsu.diff.engine.Diff
import ru.nsu.diff.engine.transforming.EditScript

private const val VIEWER_PANEL_WIDTH = 700
private const val VIEWER_PANEL_HEIGHT = 300

class DiffViewerPanel(private val project: Project) : JPanel() {

    var firstFile: VirtualFile? = null
    var secondFile: VirtualFile? = null

    init {
        preferredSize = Dimension(VIEWER_PANEL_WIDTH, VIEWER_PANEL_HEIGHT)
    }

    fun showResult() {
        if (firstFile == null || secondFile == null) {
            DiffViewerNotifier.showDialog(DiffMessageType.NO_FILES)
            return
        }
        if (firstFile!!.fileType != secondFile!!.fileType) {
            DiffViewerNotifier.showDialog(DiffMessageType.DIFFERENT_TYPES)
            return
        }
        val firstPsi = PsiManager.getInstance(project).findFile(firstFile!!)?.originalElement
        val secondPsi = PsiManager.getInstance(project).findFile(secondFile!!)?.originalElement
        if (firstPsi == null || secondPsi == null) {
            DiffViewerNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
            return
        }

        Diff.diff(firstPsi.copy(), secondPsi.copy()).render()
    }

    private fun EditScript.render() {
        // TODO: create component and add
        println(this)
    }
}