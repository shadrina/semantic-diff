package ru.nsu.diff.view.util

import com.intellij.ide.diff.DiffType
import com.intellij.openapi.diff.impl.DiffHighlighterFactoryImpl
import com.intellij.openapi.diff.impl.DiffLineMarkerRenderer
import com.intellij.openapi.diff.impl.util.LabeledEditor
import com.intellij.openapi.diff.impl.util.TextDiffType
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.util.LinesRange
import ru.nsu.diff.view.DiffSide
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.JLabel

object DiffEditorUtil {
    fun createEditorPanel(project: Project, file: VirtualFile?, side: DiffSide) : EditorEx {
        val editorFactory = EditorFactory.getInstance()
        val myEditor = if (file == null) {
            val document = editorFactory.createDocument("Nothing to show...")
            editorFactory.createEditor(document)
        } else {
            // TODO: create Formatter class
            val text = file.inputStream.bufferedReader().readText().replace("\r", "")
            val document = editorFactory.createDocument(text)
            editorFactory.createEditor(document, project, file, false)
        }
        myEditor.component.preferredSize = Dimension(DIFF_EDITOR_WIDTH, DIFF_EDITOR_HEIGHT)
        myEditor.contentComponent.isEnabled = true
        myEditor.scrollingModel.scrollHorizontally(0)
        myEditor.settings.isCaretRowShown = false

        myEditor as EditorEx
        myEditor.gutterComponentEx.setInitialIconAreaWidth(10)
        if (file != null) {
            val highlighter = DiffHighlighterFactoryImpl(file.fileType, file, project).createHighlighter()
            if (highlighter != null) myEditor.highlighter = highlighter
        }
        if (side == DiffSide.LEFT) {
            myEditor.verticalScrollbarOrientation = EditorEx.VERTICAL_SCROLLBAR_LEFT
        }

        return myEditor
    }

    fun createDiffSidePanel(editor: EditorEx) : LabeledEditor {
        val panel = LabeledEditor()
        panel.preferredSize = editor.component.preferredSize
        val label = JLabel()
        panel.setComponent(editor.component, label)

        return panel
    }

    fun paintEditor(editor: EditorEx, chunks: List<DiffChunk>, diffSide: DiffSide) {
        var linesRange: LinesRange?
        var color: JBColor
        var textAttributes: TextAttributes

        for (chunk in chunks) {
            linesRange = if (diffSide == DiffSide.LEFT) chunk.leftLines else chunk.rightLines
            color = ColorFactory.editorOperationColor(chunk.type)
            textAttributes = TextAttributes(null, color, null, null, 0)
            val start = linesRange?.startLine ?: 0
            val stop = linesRange?.stopLine ?: 0
            for (i in start..stop) {
                editor.markupModel.addLineHighlighter(i, HighlighterLayer.CARET_ROW, textAttributes)
            }
        }
    }
}