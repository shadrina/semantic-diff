package ru.nsu.diff.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper

import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel


class MainDialog(private val project: Project) : DialogWrapper(project, true, IdeModalityType.MODELESS) {
    init {
        init()
        title = "Semantic diff"
        setOKButtonText("Diff")
        setCancelButtonText("Close")
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(GridLayout(2, 1))
        panel.add(FileInputPanel(project))
        panel.add(DiffViewerPanel())

        return panel
    }

    override fun doOKAction() {
        TODO("do diff")
    }
}