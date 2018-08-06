package ru.nsu.diff.view.panels

import com.intellij.openapi.project.Project
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

class UpperPanel(project: Project, viewerPanel: DiffViewerPanel) : JPanel() {
    private val fileInputPanel = FileInputPanel(project, viewerPanel)
    private val infoPanel = InfoPanel()

    init {
        layout = GridBagLayout()
        val gc = GridBagConstraints()
        gc.gridx = 0
        gc.gridy = 0
        add(fileInputPanel, gc)
        gc.gridx = 1
        add(infoPanel, gc)

        viewerPanel.infoPanel = infoPanel
    }
}