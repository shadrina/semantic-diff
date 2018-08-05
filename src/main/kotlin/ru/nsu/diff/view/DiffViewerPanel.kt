package ru.nsu.diff.view

import com.intellij.diff.tools.util.DiffSplitter
import com.intellij.openapi.diff.impl.util.LabeledEditor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager

import javax.swing.JPanel
import java.awt.Dimension
import javax.swing.JLabel

import ru.nsu.diff.engine.Diff
import ru.nsu.diff.engine.transforming.EditScript
import ru.nsu.diff.test.DiffTester
import ru.nsu.diff.view.util.DiffEditorFactory
import java.awt.BorderLayout
import java.awt.Color

enum class DiffSide { RIGHT, LEFT }

class SemDiffVisibleAreaListener : VisibleAreaListener {
    override fun visibleAreaChanged(e: VisibleAreaEvent?) {
        println(e?.newRectangle)
    }

}

class DiffViewerPanel(private val project: Project) : JPanel() {

    var file1: VirtualFile? = null
    var file2: VirtualFile? = null

    private var leftEditor = DiffEditorFactory.createEditorPanel(project, null, DiffSide.LEFT)
    private var rightEditor = DiffEditorFactory.createEditorPanel(project, null, DiffSide.RIGHT)
    private val mockSplitter = DiffSplitter()

    init {
        layout = BorderLayout()
        mockSplitter.firstComponent = DiffEditorFactory.createLabeledEditor(leftEditor)
        mockSplitter.secondComponent = DiffEditorFactory.createLabeledEditor(rightEditor)
        add(mockSplitter, BorderLayout.CENTER)
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
        println(this)

        leftEditor = DiffEditorFactory.createEditorPanel(project, file1!!, DiffSide.LEFT)
        rightEditor = DiffEditorFactory.createEditorPanel(project, file2!!, DiffSide.RIGHT)

        val painter = DividerPainter()
        painter.leftEditor = leftEditor
        painter.rightEditor = rightEditor

        val splitter = DiffSplitter()
        splitter.firstComponent = DiffEditorFactory.createLabeledEditor(leftEditor)
        splitter.secondComponent = DiffEditorFactory.createLabeledEditor(rightEditor)
        splitter.setPainter(painter)

        remove(mockSplitter)
        add(splitter, BorderLayout.CENTER)
        revalidate()
    }
}
