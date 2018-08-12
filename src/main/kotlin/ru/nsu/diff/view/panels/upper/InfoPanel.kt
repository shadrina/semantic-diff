package ru.nsu.diff.view.panels.upper

import com.intellij.ui.components.JBLabel
import ru.nsu.diff.view.util.INFO_PANEL_WIDTH
import ru.nsu.diff.view.util.UPPER_PANEL_HEIGHT
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JPanel

class InfoPanel : JPanel() {
    var differenceCount: Int = 0
        set(value) {
            differenceCountLabel.text = value.toString()
            field = differenceCount

            labelPanel.remove(differenceCountLabel)
            differenceCountLabel = JBLabel(value.toString())
            labelPanel.add(differenceCountLabel, BorderLayout.EAST)

            revalidate()
            repaint()
        }
    private val labelPanel: JPanel = JPanel()
    private var differenceCountLabel: JBLabel = JBLabel(differenceCount.toString())

    init {
        preferredSize = Dimension(INFO_PANEL_WIDTH, UPPER_PANEL_HEIGHT)
        layout = BorderLayout()

        val label = JBLabel("Differences found: ")
        label.verticalTextPosition = 0
        differenceCountLabel.verticalTextPosition = 0

        labelPanel.layout = BorderLayout()
        labelPanel.add(label, BorderLayout.WEST)
        labelPanel.add(differenceCountLabel, BorderLayout.EAST)

        add(labelPanel, BorderLayout.EAST)
    }
}