package ru.nsu.diff.view.panels.upper

import com.intellij.openapi.project.Project
import ru.nsu.diff.view.panels.DiffViewerPanel
import ru.nsu.diff.view.util.Mode
import ru.nsu.diff.view.util.ModeChangeListener
import ru.nsu.diff.view.util.UPPER_PANEL_HEIGHT
import ru.nsu.diff.view.util.UPPER_PANEL_WIDTH
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

class UpperPanel(project: Project, viewerPanel: DiffViewerPanel) : JPanel(), ModeChangeListener {
    private val navigationPanel = NavigationPanel()
    private val selectFilesPanel = SelectFilesPanel(project, viewerPanel)
    private val browseGitRepoPanel = BrowseGitRepoPanel(project, viewerPanel)
    private val optionsPanel = OptionsPanel(viewerPanel)
    private val infoPanel = InfoPanel()

    private var myMode = Mode.SELECT_FILES
    private val changeablePanelGc = GridBagConstraints()

    init {
        preferredSize = Dimension(UPPER_PANEL_WIDTH, UPPER_PANEL_HEIGHT)

        layout = GridBagLayout()
        val gc = GridBagConstraints()
        gc.gridx = 0
        gc.gridy = 0
        add(navigationPanel, gc)
        gc.gridx = 1
        add(selectFilesPanel, gc)
        gc.gridx = 2
        add(optionsPanel, gc)
        gc.gridx = 3
        add(infoPanel, gc)

        viewerPanel.infoPanel = infoPanel
        navigationPanel.addModeChangeListener(this)

        changeablePanelGc.gridx = 1
        changeablePanelGc.gridy = 0
    }

    override fun modeChanged(newMode: Mode) {
        if (newMode == myMode) return
        if (newMode == Mode.SELECT_FILES) {
            remove(browseGitRepoPanel)
            add(selectFilesPanel, changeablePanelGc)
        }
        if (newMode == Mode.BROWSE_GIT_REPO) {
            remove(selectFilesPanel)
            add(browseGitRepoPanel, changeablePanelGc)
        }
        myMode = newMode
        revalidate()
        repaint()
    }
}