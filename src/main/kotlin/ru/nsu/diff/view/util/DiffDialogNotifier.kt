package ru.nsu.diff.view.util

import com.intellij.openapi.ui.Messages

enum class DiffMessageType() {
    NO_FILES,
    INVALID_TYPE, // TODO
    DIFFERENT_TYPES,
    NOT_GIT_REPO,
    UNABLE_TO_DIFF
}

object DiffDialogNotifier {
    fun showDialog(msgCode: DiffMessageType) {
        var msg = ""
        var title = ""

        when (msgCode) {
            DiffMessageType.NO_FILES -> {
                msg = "Specify both files to view diff"
                title = "Select files"
            }
            DiffMessageType.INVALID_TYPE -> {
                msg = "We are able now to parse .java or .kt files"
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
        }
        Messages.showMessageDialog(msg, title, Messages.getWarningIcon())
    }
}
