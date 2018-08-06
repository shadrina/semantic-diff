package ru.nsu.diff.view.panels

import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

private const val INFO_PANEL_WIDTH = 1080
private const val INFO_PANEL_HEIGHT = 90

class InfoPanel : JPanel() {
    var differenceCount: Int = 0
        set(value) {
            label.text = label.text.replace(differenceCount.toString(), value.toString())
            field = differenceCount
        }
    private val label: JBLabel = JBLabel("Differences found: $differenceCount")

    init {
        preferredSize = Dimension(INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT)
        layout = BorderLayout()
        label.verticalTextPosition = 0

        add(label, BorderLayout.EAST)
    }


}