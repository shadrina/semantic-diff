package ru.nsu.diff.view

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager

import java.awt.Dimension
import javax.swing.JPanel

import ru.nsu.diff.engine.Diff
import ru.nsu.diff.engine.transforming.EditScript
import ru.nsu.diff.test.DiffTester
import ru.nsu.diff.test.IntellijDiff
import kotlin.streams.toList

private const val VIEWER_PANEL_WIDTH = 700
private const val VIEWER_PANEL_HEIGHT = 300

class DiffViewerPanel(private val project: Project) : JPanel() {

    var file1: VirtualFile? = null
    var file2: VirtualFile? = null

    init {
        preferredSize = Dimension(VIEWER_PANEL_WIDTH, VIEWER_PANEL_HEIGHT)
    }

    fun showResult() {
        if (file1 == null || file2 == null) {
            DiffViewerNotifier.showDialog(DiffMessageType.NO_FILES)
            return
        }
        if (file1!!.fileType != file2!!.fileType) {
            DiffViewerNotifier.showDialog(DiffMessageType.DIFFERENT_TYPES)
            return
        }
        val psi1 = PsiManager.getInstance(project).findFile(file1!!)?.originalElement
        val psi2 = PsiManager.getInstance(project).findFile(file2!!)?.originalElement
        if (psi1 == null || psi2 == null) {
            DiffViewerNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
            return
        }

        val script = Diff.diff(psi1, psi2)
        if (script == null) DiffViewerNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
        else {
            script.render()
            DiffTester.test(file1!!, file2!!, script)
        }
    }

    private fun EditScript.render() {
        // TODO: create component and add
        println(this)
        IntellijDiff.showDiffForFiles(project, file1!!, file2!!)
    }
}

