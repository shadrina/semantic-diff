package ru.nsu.diff.view.util

import com.intellij.openapi.diff.impl.DiffHighlighterFactoryImpl
import com.intellij.openapi.diff.impl.util.LabeledEditor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import com.intellij.util.DocumentUtil
import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.view.panels.DiffSide
import java.awt.Color
import java.awt.Dimension
import javax.swing.JLabel

object DiffEditorUtil {
    fun createEditorPanelFromPsiFile(project: Project, psiFile: PsiFile?, side: DiffSide) : EditorEx {
        val editorFactory = EditorFactory.getInstance()
        val myEditor = if (psiFile == null) {
            val document = editorFactory.createDocument("Nothing to show...")
            editorFactory.createEditor(document)
        } else {
            val text = psiFile.text
            val document = editorFactory.createDocument(text)
            editorFactory.createEditor(document, project, psiFile.fileType, false)
        }
        myEditor.component.preferredSize = Dimension(DIFF_EDITOR_WIDTH, DIFF_EDITOR_HEIGHT)
        myEditor.contentComponent.isEnabled = true
        myEditor.scrollingModel.scrollHorizontally(0)
        myEditor.settings.isCaretRowShown = false

        myEditor as EditorEx
        myEditor.gutterComponentEx.setInitialIconAreaWidth(10)
        if (side == DiffSide.LEFT) {
            myEditor.verticalScrollbarOrientation = EditorEx.VERTICAL_SCROLLBAR_LEFT
        }

        return myEditor
    }

    fun createEditorPanelFromVirtualFile(project: Project, file: VirtualFile?, side: DiffSide) : EditorEx {
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
        var textRange: TextRange?
        var color: JBColor
        var textAttributes: TextAttributes

        for (chunk in chunks) {
            textRange = if (diffSide == DiffSide.LEFT) chunk.leftRange else chunk.rightRange
            color = ColorFactory.editorOperationColor(chunk.type)
            textAttributes = TextAttributes(null, color, null, null, 0)

            if (textRange === null) continue
            val start = textRange.startOffset
            val stop = textRange.endOffset

            editor.markupModel.addRangeHighlighter(
                    start,
                    stop,
                    HighlighterLayer.CARET_ROW,
                    textAttributes,
                    HighlighterTargetArea.LINES_IN_RANGE
            )
            if (editor.document.getLineNumber(start) == editor.document.getLineNumber(stop) &&
                    (!DocumentUtil.isAtLineStart(start, editor.document)
                            || !DocumentUtil.isAtLineEnd(stop, editor.document))) {
                val ta = TextAttributes(null, ColorFactory.selectionColor(color), null, null, 0)
                editor.markupModel.addRangeHighlighter(
                        start,
                        stop,
                        HighlighterLayer.CARET_ROW,
                        ta,
                        HighlighterTargetArea.EXACT_RANGE
                )
            }
        }
    }
}