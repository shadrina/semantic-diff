package ru.nsu.diff.test

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object IntellijDiff : Disposable {
    override fun dispose() {}

    fun showDiffForFiles(project: Project, file1: VirtualFile, file2: VirtualFile) {
        val file1Content = DiffContentFactory.getInstance().create(project, file1)
        val file2Content = DiffContentFactory.getInstance().create(project, file2)

        val request = SimpleDiffRequest("Title",
                file1Content,
                file2Content,
                "File 1 - " + file1.name,
                "File 2 - " + file2.name
        )

        ApplicationManager.getApplication().invokeLater({
            ApplicationManager.getApplication().runWriteAction {
                DiffManager.getInstance().showDiff(project, request)
            }
        }, ModalityState.NON_MODAL)
    }
}