package ru.nsu.diff.view

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.ComponentWithBrowseButton

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JTextField

private const val FILE_CHOOSER_WIDTH = 35

class FileInputPanel(val project: Project, val viewerPanel: DiffViewerPanel) : JPanel() {

    private val firstFileChooser: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val secondFileChooser: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        firstFileChooser.addBrowseFolderListener(
                project,
                generateBrowseActionListener(
                        "Select first file",
                        firstFileChooser
                ) { viewerPanel.firstFile = it }
        )
        secondFileChooser.addBrowseFolderListener(
                project,
                generateBrowseActionListener(
                        "Select second file",
                        secondFileChooser
                ) { viewerPanel.secondFile = it }
        )

        firstFileChooser.setTextFieldPreferredWidth(FILE_CHOOSER_WIDTH)
        secondFileChooser.setTextFieldPreferredWidth(FILE_CHOOSER_WIDTH)

        layout = GridBagLayout()

        val gc = GridBagConstraints()

        gc.anchor = GridBagConstraints.LINE_END
        gc.gridx = 0
        gc.gridy = 0
        add(JLabel("Select first file  "), gc)
        gc.gridy = 1
        add(JLabel("Select second file  "), gc)

        gc.anchor = GridBagConstraints.CENTER
        gc.gridx = 1
        gc.gridy = 0
        add(firstFileChooser, gc)
        gc.gridy = 1
        add(secondFileChooser, gc)
    }

    private fun generateBrowseActionListener(
            title: String,
            fileChooser: TextFieldWithBrowseButton,
            onFileChosen: (VirtualFile) -> Unit
    ) : ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {

        val singleFileDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        return object : ComponentWithBrowseButton.BrowseFolderActionListener<JTextField>(
                title,
                null,
                fileChooser,
                project,
                singleFileDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        ) {
            override fun onFileChosen(chosenFile: VirtualFile) {
                super.onFileChosen(chosenFile)
                onFileChosen(chosenFile)
            }
        }
    }
}