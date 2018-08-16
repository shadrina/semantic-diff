package ru.nsu.diff.view.util

import com.intellij.openapi.ui.Messages

enum class DiffMessageType() {
    NO_FILES,
    UNSUPPORTED_TYPE,
    DIFFERENT_TYPES,
    NOT_GIT_REPO,
    UNABLE_TO_DIFF,
    IDENTICAL_FILES
}

object DiffDialogNotifier {
    fun showDialog(msgCode: DiffMessageType) {
        var msg = ""
        var title = ""
        var icon = Messages.getWarningIcon()

        when (msgCode) {
            DiffMessageType.NO_FILES -> {
                msg = "Specify both files to view diff"
                title = "Select files"
            }
            DiffMessageType.UNSUPPORTED_TYPE -> {
                msg = "Such files are not supported yet"
                title = "Invalid file type"
            }
            DiffMessageType.DIFFERENT_TYPES -> {
                msg = "Select files with the same file type"
                title = "Different file types"
            }
            DiffMessageType.NOT_GIT_REPO -> {
                msg = "Select git repository"
                title = ".git not found"
            }
            DiffMessageType.UNABLE_TO_DIFF -> {
                msg = "Unable to diff files"
                title = "Problem"
            }
            DiffMessageType.IDENTICAL_FILES -> {
                msg = "Files are identical"
                title = "No differences"
                icon = Messages.getInformationIcon()
            }
        }
        Messages.showMessageDialog(msg, title, icon)
    }
}
