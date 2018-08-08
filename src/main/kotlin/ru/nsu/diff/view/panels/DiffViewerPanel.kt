package ru.nsu.diff.view.panels

import com.intellij.diff.tools.util.DiffSplitter
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
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
import ru.nsu.diff.test.DiffTester
import ru.nsu.diff.view.panels.upper.InfoPanel
import ru.nsu.diff.view.util.*

enum class DiffSide { RIGHT, LEFT }

class DiffViewerPanel(private val project: Project) : JPanel() {
    lateinit var infoPanel: InfoPanel

    var file1: VirtualFile? = null
        set(value) {
            field = value
            updateEditor(DiffSide.LEFT)
        }
    var file2: VirtualFile? = null
        set(value) {
            field = value
            updateEditor(DiffSide.RIGHT)
        }

    private lateinit var leftEditor: EditorEx
    private lateinit var rightEditor: EditorEx

    private lateinit var leftLayerUI: GutterLayerUI
    private lateinit var rightLayerUI: GutterLayerUI

    private val splitter = DiffSplitter()
    private val painter = DividerPainter(splitter)

    init {
        layout = BorderLayout()
        border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY, 1)

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
                leftEditor = DiffEditorUtil.createEditorPanel(project, file1, side)
                UIUtil.removeScrollBorder(leftEditor.component)

                val labeled = DiffEditorUtil.createDiffSidePanel(leftEditor)
                leftLayerUI = GutterLayerUI(painter, labeled, leftEditor, side)
                val jLayer = JLayer<JComponent>(labeled, leftLayerUI)

                splitter.firstComponent = jLayer
            }
            DiffSide.RIGHT -> {
                rightEditor = DiffEditorUtil.createEditorPanel(project, file2, side)
                UIUtil.removeScrollBorder(rightEditor.component)

                val labeled = DiffEditorUtil.createDiffSidePanel(rightEditor)
                rightLayerUI = GutterLayerUI(painter, labeled, rightEditor, side)
                val jLayer = JLayer<JComponent>(labeled, rightLayerUI)

                splitter.secondComponent = jLayer
            }
        }
    }

    fun showResult() {
        if (file1 === null || file2 === null) {
            DiffDialogNotifier.showDialog(DiffMessageType.NO_FILES)
            return
        }
        if (file1!!.fileType !== file2!!.fileType) {
            DiffDialogNotifier.showDialog(DiffMessageType.DIFFERENT_TYPES)
            return
        }
        val psi1 = PsiManager.getInstance(project).findFile(file1!!)?.originalElement
        val psi2 = PsiManager.getInstance(project).findFile(file2!!)?.originalElement
        if (psi1 === null || psi2 === null) {
            DiffDialogNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
            return
        }

        val chunks = Diff.diff(psi1, psi2)
        when {
            chunks == null -> DiffDialogNotifier.showDialog(DiffMessageType.UNABLE_TO_DIFF)
            chunks.isEmpty() -> DiffDialogNotifier.showDialog(DiffMessageType.IDENTICAL_FILES)
            else -> {
                chunks.render()
                DiffTester.test(file1!!, file2!!, chunks)
            }
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
