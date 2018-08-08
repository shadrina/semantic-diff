package ru.nsu.diff.view.panels.upper

import com.intellij.ui.components.JBLabel
import ru.nsu.diff.view.util.INFO_PANEL_WIDTH
import ru.nsu.diff.view.util.UPPER_PANEL_HEIGHT
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

class InfoPanel : JPanel() {
    var differenceCount: Int = 0
        set(value) {
            label.text = label.text.replace(differenceCount.toString(), value.toString())
            field = differenceCount
        }
    private val label: JBLabel = JBLabel("Differences found: $differenceCount ")

    init {
        preferredSize = Dimension(INFO_PANEL_WIDTH, UPPER_PANEL_HEIGHT)
        layout = BorderLayout()
        label.verticalTextPosition = 0

        add(label, BorderLayout.EAST)
    }
}