package ru.nsu.diff.view.panels.upper

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import org.eclipse.jgit.lib.Repository
import ru.nsu.diff.git.DiffTestDataProvider
import ru.nsu.diff.git.DiffTestDataProvider.buildGitRepositoryFromVirtualFile
import ru.nsu.diff.git.FileVersions

import java.awt.Dimension
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout

import ru.nsu.diff.view.panels.DiffViewerPanel
import ru.nsu.diff.view.util.*
import java.io.File
import java.io.FileOutputStream



private const val FILE_CHOOSER_WIDTH = 40

class BrowseGitRepoPanel(project: Project, viewerPanel: DiffViewerPanel) : JPanel() {
    private val repositoryChooser: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val fileChooser: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    private var v1ComboBox: ComboBox<String> = ComboBox()
    private var v2ComboBox: ComboBox<String> = ComboBox()

    private var gitRepository: Repository? = null
    private var chosenFileVersions: FileVersions? = null

    init {
        preferredSize = Dimension(CHANGEABLE_PANEL_WIDTH, UPPER_PANEL_HEIGHT)
        val leftSidedPanel = JPanel()
        leftSidedPanel.layout = GridBagLayout()

        repositoryChooser.addBrowseFolderListener(
                project,
                BrowseActionListenerFactory.generate(
                        project,
                        "Select git repository",
                        repositoryChooser,
                        FileChooserDescriptorFactory.createSingleFolderDescriptor()
                ) {
                    gitRepository = buildGitRepositoryFromVirtualFile(it)
                    if (gitRepository == null) DiffDialogNotifier.showDialog(DiffMessageType.NOT_GIT_REPO)
                }
        )
        fileChooser.addBrowseFolderListener(
                project,
                BrowseActionListenerFactory.generate(
                        project,
                        "Select file",
                        fileChooser,
                        FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                ) { chosenFile ->
                    if (chosenFile.extension != "kt" && chosenFile.extension != "java") {
                        DiffDialogNotifier.showDialog(DiffMessageType.INVALID_TYPE)
                    }
                    if (gitRepository != null) {
                        chosenFileVersions = DiffTestDataProvider.getTestDataFromRepository(
                                gitRepository!!,
                                chosenFile.extension ?: "*",
                                chosenFile.path
                        ).firstOrNull()

                        val hashes = chosenFileVersions?.versions?.map { it.hash.substring(0, 5) }
                        if (hashes != null) {
                            leftSidedPanel.remove(v1ComboBox)
                            leftSidedPanel.remove(v2ComboBox)
                            v1ComboBox = ComboBox(hashes.toTypedArray())
                            v2ComboBox = ComboBox(hashes.toTypedArray())

                            val gc = GridBagConstraints()
                            gc.gridx = 3
                            gc.gridy = 0
                            leftSidedPanel.add(v1ComboBox, gc)
                            gc.gridy = 1
                            leftSidedPanel.add(v2ComboBox, gc)

                            leftSidedPanel.revalidate()
                            leftSidedPanel.repaint()

                            v1ComboBox.addActionListener {
                                val cb = it.source
                                if (cb is ComboBox<*>) {
                                    val selected = cb.selectedItem
                                    val bytes = chosenFileVersions!!.versions
                                            .find { it.hash.startsWith(selected as String) }
                                            ?.bytes
                                    val vf = createDummyFileFromBytes(bytes, "Dummy1.${chosenFile.extension}")
                                    viewerPanel.file1 = vf
                                }
                            }
                            v2ComboBox.addActionListener {
                                val cb = it.source
                                if (cb is ComboBox<*>) {
                                    val selected = cb.selectedItem
                                    val bytes = chosenFileVersions!!.versions
                                            .find { it.hash.startsWith(selected as String) }
                                            ?.bytes
                                    val vf = createDummyFileFromBytes(bytes, "Dummy2.${chosenFile.extension}")
                                    viewerPanel.file2 = vf
                                }
                            }
                        }
                    }
                }
        )

        repositoryChooser.setTextFieldPreferredWidth(FILE_CHOOSER_WIDTH)
        fileChooser.setTextFieldPreferredWidth(FILE_CHOOSER_WIDTH)

        val gc = GridBagConstraints()

        gc.anchor = GridBagConstraints.LINE_END
        gc.gridx = 0
        gc.gridy = 0
        leftSidedPanel.add(JBLabel("   Select repo  "), gc)
        gc.gridy = 1
        leftSidedPanel.add(JBLabel("   Select file  "), gc)

        gc.anchor = GridBagConstraints.CENTER
        gc.gridx = 1
        gc.gridy = 0
        leftSidedPanel.add(repositoryChooser, gc)
        gc.gridy = 1
        leftSidedPanel.add(fileChooser, gc)

        gc.anchor = GridBagConstraints.LINE_END
        gc.gridx = 2
        gc.gridy = 0
        leftSidedPanel.add(JBLabel("   v1  "), gc)
        gc.gridy = 1
        leftSidedPanel.add(JBLabel("   v2  "), gc)

        gc.anchor = GridBagConstraints.CENTER
        gc.gridx = 3
        gc.gridy = 0
        leftSidedPanel.add(v1ComboBox, gc)
        gc.gridy = 1
        leftSidedPanel.add(v2ComboBox, gc)

        layout = BorderLayout()
        add(leftSidedPanel, BorderLayout.WEST)
    }

    // TODO: temporary --- do not create files!
    private fun createDummyFileFromBytes(bytes: ByteArray?, fileName: String) : VirtualFile? {
        if (bytes == null) return null
        val path = "D:/tmp/$fileName"
        FileOutputStream(path).use { fos ->
            fos.write(bytes)
        }
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(path))
    }
}