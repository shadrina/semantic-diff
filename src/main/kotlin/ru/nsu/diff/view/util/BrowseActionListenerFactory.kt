package ru.nsu.diff.view.util

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JTextField

object BrowseActionListenerFactory {
    fun generate(
            project: Project,
            title: String,
            fileChooser: TextFieldWithBrowseButton,
            descriptor: FileChooserDescriptor,
            onFileChosen: (VirtualFile) -> Unit
    ) : ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {
        return object : ComponentWithBrowseButton.BrowseFolderActionListener<JTextField>(
                title,
                null,
                fileChooser,
                project,
                descriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        ) {
            override fun onFileChosen(chosenFile: VirtualFile) {
                super.onFileChosen(chosenFile)
                onFileChosen(chosenFile)
            }
        }
    }
}