package ru.nsu.diff.view.panels.upper

import ru.nsu.diff.view.util.Mode
import ru.nsu.diff.view.util.ModeChangeListener
import ru.nsu.diff.view.util.NAVIGATION_PANEL_WIDTH

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JPanel

class NavigationPanel : JPanel() {
    private val listeners: MutableList<ModeChangeListener> = mutableListOf()

    init {
        val selectFilesButton = JButton("Select files")
        selectFilesButton.addActionListener {
            listeners.forEach { it.modeChanged(Mode.SELECT_FILES) }
        }
        val browseGitRepoButton = JButton("Browse git repo")
        browseGitRepoButton.addActionListener {
            listeners.forEach { it.modeChanged(Mode.BROWSE_GIT_REPO) }
         }

        selectFilesButton.preferredSize = browseGitRepoButton.preferredSize
        preferredSize = Dimension(
                NAVIGATION_PANEL_WIDTH,
                selectFilesButton.preferredSize.height * 2)

        layout = BorderLayout()
        add(selectFilesButton, BorderLayout.NORTH)
        add(browseGitRepoButton, BorderLayout.SOUTH)
    }

    fun addModeChangeListener(listener: ModeChangeListener) {
        listeners.add(listener)
    }

    fun removeModeChangeListener(listener: ModeChangeListener) {
        listeners.remove(listener)
    }
}