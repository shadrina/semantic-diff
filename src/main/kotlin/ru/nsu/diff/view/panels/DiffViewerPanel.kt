package ru.nsu.diff.view.panels

import com.intellij.diff.tools.util.DiffSplitter
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil

import javax.swing.JPanel
import javax.swing.JComponent
import javax.swing.JLayer
import javax.swing.BorderFactory
import java.awt.BorderLayout

import ru.nsu.diff.engine.Diff
import ru.nsu.diff.engine.conversion.DiffChunk
import ru.nsu.diff.view.panels.upper.InfoPanel
import ru.nsu.diff.view.util.*

enum class DiffSide { RIGHT, LEFT }

class DiffViewerPanel(private val project: Project) : JPanel() {
    lateinit var infoPanel: InfoPanel

    var file1: VirtualFile? = null
        set(value) {
            field = value
            updateEditor(DiffSide.LEFT)
            clearHighlighting(DiffSide.RIGHT)
        }
    var file2: VirtualFile? = null
        set(value) {
            field = value
            updateEditor(DiffSide.RIGHT)
            clearHighlighting(DiffSide.LEFT)
        }
    var psi1: PsiFile? = null
        set(value) {
            field = value
            updateEditor(DiffSide.LEFT)
            clearHighlighting(DiffSide.RIGHT)
        }
    var psi2: PsiFile? = null
        set(value) {
            field = value
            updateEditor(DiffSide.RIGHT)
            clearHighlighting(DiffSide.LEFT)
        }

    private lateinit var leftEditor: EditorEx
    private lateinit var rightEditor: EditorEx

    private lateinit var leftLayerUI: GutterLayerUI
    private lateinit var rightLayerUI: GutterLayerUI

    private val splitter = DiffSplitter()
    private val painter = DividerPainter(splitter)

    init {
        layout = BorderLayout()
        border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY, 2)

        updateEditor(DiffSide.LEFT)
        updateEditor(DiffSide.RIGHT)

        painter.leftEditor = leftEditor
        painter.rightEditor = rightEditor
        splitter.setPainter(painter)

        add(splitter, BorderLayout.CENTER)
    }

    private fun updateEditor(side: DiffSide) {
        when (side) {
            DiffSide.LEFT -> {
                leftEditor =
                        if (file1 != null) DiffEditorUtil.createEditorPanelFromVirtualFile(project, file1, side)
                        else DiffEditorUtil.createEditorPanelFromPsiFile(project, psi1, side)
                UIUtil.removeScrollBorder(leftEditor.component)

                val labeled = DiffEditorUtil.createDiffSidePanel(leftEditor)
                leftLayerUI = GutterLayerUI(painter, labeled, leftEditor, side)
                val jLayer = JLayer<JComponent>(labeled, leftLayerUI)

                splitter.firstComponent = jLayer
                painter.leftEditor = leftEditor
            }
            DiffSide.RIGHT -> {
                rightEditor =
                        if (file2 != null) DiffEditorUtil.createEditorPanelFromVirtualFile(project, file2, side)
                        else DiffEditorUtil.createEditorPanelFromPsiFile(project, psi2, side)
                UIUtil.removeScrollBorder(rightEditor.component)

                val labeled = DiffEditorUtil.createDiffSidePanel(rightEditor)
                rightLayerUI = GutterLayerUI(painter, labeled, rightEditor, side)
                val jLayer = JLayer<JComponent>(labeled, rightLayerUI)

                splitter.secondComponent = jLayer
                painter.rightEditor = rightEditor
            }
        }
        painter.chunks = listOf()
    }

    private fun clearHighlighting(side: DiffSide) {
        when (side) {
            DiffSide.LEFT -> leftEditor.markupModel.removeAllHighlighters()
            DiffSide.RIGHT -> rightEditor.markupModel.removeAllHighlighters()
        }
        painter.chunks = listOf()
        leftLayerUI.chunks = listOf()
        rightLayerUI.chunks = listOf()
    }

    fun showResult() {
        val fileRepresentation1 = file1 ?: psi1
        val fileType1 = file1?.fileType ?: psi1?.fileType

        val fileRepresentation2 = file2 ?: psi2
        val fileType2 = file2?.fileType ?: psi2?.fileType

        if (fileRepresentation1 === null || fileRepresentation2 === null) {
            DiffDialogNotifier.showDialog(DiffMessageType.NO_FILES)
            return
        }
        if (fileType1 != fileType2) {
            DiffDialogNotifier.showDialog(DiffMessageType.DIFFERENT_TYPES)
            return
        }
        val psiElement1 = (if (file1 !== null) PsiManager.getInstance(project).findFile(file1!!) else psi1)
                ?.originalElement
        val psiElement2 = (if (file2 !== null) PsiManager.getInstance(project).findFile(file2!!) else psi2)
                ?.originalElement
        if (psiElement1 === null || psiElement2 === null) {
            DiffDialogNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
            return
        }


        val chunks = Diff.diff(psiElement1, psiElement2)
        when {
            chunks === null -> DiffDialogNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
            chunks.isEmpty() -> DiffDialogNotifier.showDialog(DiffMessageType.IDENTICAL_FILES)
            else -> chunks.render()
        }
    }

    private fun List<DiffChunk>.render() {
        this.forEach(::println)

        infoPanel.differenceCount = this.size

        DiffEditorUtil.paintEditor(leftEditor, this, DiffSide.LEFT)
        DiffEditorUtil.paintEditor(rightEditor, this, DiffSide.RIGHT)

        leftLayerUI.chunks = this
        rightLayerUI.chunks = this
        painter.chunks = this

        splitter.repaintDivider()
    }
}
