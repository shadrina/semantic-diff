package ru.nsu.diff.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import ru.nsu.diff.view.panels.DiffViewerPanel
import ru.nsu.diff.view.panels.upper.UpperPanel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout

import javax.swing.JComponent
import javax.swing.JPanel

class MainDialog(project: Project) : DialogWrapper(project, true, IdeModalityType.MODELESS) {
    private val diffViewerPanel = DiffViewerPanel(project)
    private val upperPanel = UpperPanel(project, diffViewerPanel)

    init {
        init()
        title = "Semantic diff"
        // TODO: make resizable with editor size change
        setResizable(false)
        setOKButtonText("Diff")
        setCancelButtonText("Close")
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(GridBagLayout())

        val gc = GridBagConstraints()
        gc.gridx = 0
        gc.gridy = 0
        panel.add(upperPanel, gc)
        gc.gridy = 1
        panel.add(diffViewerPanel, gc)

        return panel
    }

    override fun doOKAction() {
        diffViewerPanel.showResult()
    }
}