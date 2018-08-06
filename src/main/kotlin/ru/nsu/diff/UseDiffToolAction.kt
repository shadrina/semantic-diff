package ru.nsu.diff

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

import ru.nsu.diff.view.MainDialog

class UseDiffToolAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val presentation = e.presentation

        if (project === null) {
            presentation.isEnabledAndVisible = false
            return
        }

        MainDialog(project).show()
    }
}