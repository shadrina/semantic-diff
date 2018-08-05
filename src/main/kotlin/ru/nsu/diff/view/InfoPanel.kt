package ru.nsu.diff.view

import java.awt.Dimension
import javax.swing.JPanel

private const val INFO_PANEL_WIDTH = 1060
private const val INFO_PANEL_HEIGHT = 90

class InfoPanel : JPanel() {
    init {
        preferredSize = Dimension(INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT)
    }
}