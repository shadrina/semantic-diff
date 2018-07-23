package ru.nsu.diff.view

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory

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

        val firstFileType = firstFile!!.fileType
        val secondFileType = secondFile!!.fileType
        if (firstFileType != secondFileType) {
            DiffViewerNotifier.showDialog(DiffMessageType.DIFFERENT_TYPES)
            return
        }

        val firstFileExtension = firstFile!!.extension
        val secondFileExtension = secondFile!!.extension

        val firstFileContent = firstFile!!.inputStream.bufferedReader().readText()
        val secondFileContent = secondFile!!.inputStream.bufferedReader().readText()

        val firstPsi = PsiFileFactory
                .getInstance(project)
                .createFileFromText("First.$firstFileExtension", firstFileType, firstFileContent)
        val secondPsi = PsiFileFactory
                .getInstance(project)
                .createFileFromText("Second.$secondFileExtension", secondFileType, secondFileContent)

        Diff.diff(firstPsi, secondPsi).render()
    }

    private fun EditScript.render() {
        // TODO: create component and add
    }
}