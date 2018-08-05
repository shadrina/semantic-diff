package ru.nsu.diff.view.util

import com.intellij.openapi.diff.impl.util.LabeledEditor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ru.nsu.diff.view.DiffSide
import ru.nsu.diff.view.SemDiffVisibleAreaListener
import java.awt.Dimension
import javax.swing.JLabel

object DiffEditorFactory {
    private const val EDITOR_WIDTH = 750
    private const val EDITOR_HEIGHT = 380

    fun createEditorPanel(project: Project, file: VirtualFile?, side: DiffSide) : EditorEx {
        val editorFactory = EditorFactory.getInstance()
        val myEditor = if (file == null) {
            val document = editorFactory.createDocument("")
            editorFactory.createEditor(document)
        } else {
            // TODO: create Formatter class
            val text = file.inputStream.bufferedReader().readText().replace("\r", "")
            val document = editorFactory.createDocument(text)
            editorFactory.createEditor(document, project, file, false)
        }
        myEditor.component.preferredSize = Dimension(EDITOR_WIDTH, EDITOR_HEIGHT)
        myEditor.contentComponent.isEnabled = true
        myEditor.scrollingModel.addVisibleAreaListener(SemDiffVisibleAreaListener())
        if (side == DiffSide.LEFT) {
            (myEditor as EditorEx).verticalScrollbarOrientation = EditorEx.VERTICAL_SCROLLBAR_LEFT
        }

        return myEditor as EditorEx
    }

    fun createLabeledEditor(editor: EditorEx) : LabeledEditor {
        val labeled = LabeledEditor()
        val label = JLabel()
        labeled.setComponent(editor.component, label)

        return labeled
    }
}