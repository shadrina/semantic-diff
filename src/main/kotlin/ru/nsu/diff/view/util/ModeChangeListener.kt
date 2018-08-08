package ru.nsu.diff.view.util

enum class Mode { SELECT_FILES, BROWSE_GIT_REPO }

interface ModeChangeListener {
    fun modeChanged(newMode: Mode)
}