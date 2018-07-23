package ru.nsu.diff.view

import java.awt.Dimension
import javax.swing.JPanel

private const val VIEWER_PANEL_WIDTH = 700
private const val VIEWER_PANEL_HEIGHT = 300

class DiffViewerPanel : JPanel() {
    init {
        preferredSize = Dimension(VIEWER_PANEL_WIDTH, VIEWER_PANEL_HEIGHT)
    }
}