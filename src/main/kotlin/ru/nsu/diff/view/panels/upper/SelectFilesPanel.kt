package ru.nsu.diff.view.panels.upper

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import ru.nsu.diff.view.panels.DiffViewerPanel
import ru.nsu.diff.view.util.BrowseActionListenerFactory

import ru.nsu.diff.view.util.CHANGEABLE_PANEL_WIDTH
import ru.nsu.diff.view.util.UPPER_PANEL_HEIGHT

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

private const val FILE_CHOOSER_WIDTH = 40

class SelectFilesPanel(project: Project, viewerPanel: DiffViewerPanel) : JPanel() {

    private val firstFileChooser: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val secondFileChooser: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        firstFileChooser.addBrowseFolderListener(
                project,
                BrowseActionListenerFactory.generate(
                        project,
                        "Select first file",
                        firstFileChooser,
                        FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                ) { viewerPanel.file1 = it }
        )
        secondFileChooser.addBrowseFolderListener(
                project,
                BrowseActionListenerFactory.generate(
                        project,
                        "Select second file",
                        secondFileChooser,
                        FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                ) { viewerPanel.file2 = it }
        )

        preferredSize = Dimension(CHANGEABLE_PANEL_WIDTH, UPPER_PANEL_HEIGHT)
        firstFileChooser.setTextFieldPreferredWidth(FILE_CHOOSER_WIDTH)
        secondFileChooser.setTextFieldPreferredWidth(FILE_CHOOSER_WIDTH)

        val leftSidedPanel = JPanel()
        leftSidedPanel.layout = GridBagLayout()

        val gc = GridBagConstraints()

        gc.anchor = GridBagConstraints.LINE_END
        gc.gridx = 0
        gc.gridy = 0
        leftSidedPanel.add(JBLabel("   Select file  "), gc)
        gc.gridy = 1
        leftSidedPanel.add(JBLabel("   Select file  "), gc)

        gc.anchor = GridBagConstraints.CENTER
        gc.gridx = 1
        gc.gridy = 0
        leftSidedPanel.add(firstFileChooser, gc)
        gc.gridy = 1
        leftSidedPanel.add(secondFileChooser, gc)

        layout = BorderLayout()
        add(leftSidedPanel, BorderLayout.WEST)
    }
}