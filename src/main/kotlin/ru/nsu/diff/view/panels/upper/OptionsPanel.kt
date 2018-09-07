package ru.nsu.diff.view.panels.upper

import com.intellij.ui.components.JBLabel

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.JCheckBox
import javax.swing.JPanel

import ru.nsu.diff.view.panels.DiffSide
import ru.nsu.diff.view.panels.DiffViewerPanel
import ru.nsu.diff.view.util.OPTIONS_PANEL_WIDTH
import ru.nsu.diff.view.util.UPPER_PANEL_HEIGHT

class OptionsPanel(private val diffViewerPanel: DiffViewerPanel) : JPanel() {

    init {
        preferredSize = Dimension(OPTIONS_PANEL_WIDTH, UPPER_PANEL_HEIGHT)
        layout = GridBagLayout()

        val syncScrollOption = JCheckBox("Sync scroll")
        syncScrollOption.isSelected = false
        syncScrollOption.addItemListener {
            diffViewerPanel.masterSide =
                    if (it.stateChange == ItemEvent.SELECTED) DiffSide.RIGHT
                    else null
            // TODO: add radio button with master side selection
        }

        val lineNumbersOption = JCheckBox("Line numbers")
        lineNumbersOption.isSelected = true
        lineNumbersOption.addItemListener {
            diffViewerPanel.lineNumbersOption = it.stateChange == ItemEvent.SELECTED
        }

        val softWrapsOption = JCheckBox("Soft wraps")
        softWrapsOption.isSelected = false
        softWrapsOption.addItemListener {
            diffViewerPanel.softWrapsOption = it.stateChange == ItemEvent.SELECTED
        }

        val gc = GridBagConstraints()
        gc.gridx = 0
        gc.gridy = 0
        add(JBLabel("Options:  "), gc)
        gc.gridx = 1
        gc.anchor = GridBagConstraints.LINE_START
        add(lineNumbersOption, gc)
        gc.gridy = 1
        add(syncScrollOption, gc)
        gc.gridx = 2
        gc.gridy = 0
        add(softWrapsOption, gc)
    }
}