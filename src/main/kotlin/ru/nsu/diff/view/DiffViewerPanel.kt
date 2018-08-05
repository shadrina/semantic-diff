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
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JLabel

import ru.nsu.diff.engine.Diff
import ru.nsu.diff.engine.transforming.EditScript
import ru.nsu.diff.test.DiffTester


private const val VIEWER_PANEL_WIDTH = 1550
private const val VIEWER_PANEL_HEIGHT = 400

private const val EDITOR_WIDTH = 600
private const val EDITOR_HEIGHT = 300

enum class Side {
    RIGHT,
    LEFT
}

class SemDiffVisibleAreaListener : VisibleAreaListener {
    override fun visibleAreaChanged(e: VisibleAreaEvent?) {
        println(e?.newRectangle)
    }

}

class DiffViewerPanel(private val project: Project) : JPanel() {

    var file1: VirtualFile? = null
    var file2: VirtualFile? = null

    init {
        preferredSize = Dimension(VIEWER_PANEL_WIDTH, VIEWER_PANEL_HEIGHT)
        layout = BorderLayout()
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

    private fun createEditorPanel(file: VirtualFile, side: Side) : EditorEx {
        val editorFactory = EditorFactory.getInstance()
        // TODO: create Formatter class
        val text = file.inputStream.bufferedReader().readText().replace("\r", "")
        val document = editorFactory.createDocument(text)
        val editor = editorFactory.createEditor(document, project, file1!!, false)

        editor.component.preferredSize = Dimension(EDITOR_WIDTH, EDITOR_HEIGHT)
        editor.contentComponent.isEnabled = true
        editor.scrollingModel.addVisibleAreaListener(SemDiffVisibleAreaListener())
        if (side == Side.LEFT) {
            (editor as EditorEx).verticalScrollbarOrientation = EditorEx.VERTICAL_SCROLLBAR_LEFT
        }

        return editor as EditorEx
    }

    private fun createLabeledEditor(editor: EditorEx) : LabeledEditor {
        val labeled = LabeledEditor()
        val label = JLabel()
        labeled.setComponent(editor.component, label)

        return labeled
    }

    private fun EditScript.render() {
        println(this)

        val editor1 = createEditorPanel(file1!!, Side.LEFT)
        val editor2 = createEditorPanel(file2!!, Side.RIGHT)

        val left = createLabeledEditor(editor1)
        val right = createLabeledEditor(editor2)

        val splitter = DiffSplitter()
        splitter.firstComponent = left
        splitter.secondComponent = right

        val painter = DividerPainter()
        painter.leftEditor = editor1
        painter.rightEditor = editor2
        splitter.setPainter(painter)

        add(splitter, BorderLayout.CENTER)
        revalidate()
    }
}
