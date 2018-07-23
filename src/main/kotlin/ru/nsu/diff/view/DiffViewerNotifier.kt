package ru.nsu.diff.view

import com.intellij.openapi.ui.Messages

enum class DiffMessageType(val code: Int) {
    NO_FILES(0),
    INVALID_TYPE(1), // TODO
    DIFFERENT_TYPES(2)
}

object DiffViewerNotifier {
    fun showDialog(msgCode: DiffMessageType) {
        var msg = ""
        var title = ""

        when (msgCode) {
            DiffMessageType.NO_FILES -> {
                msg = "Specify both files to view diff"
                title = "Select files"
            }
            DiffMessageType.INVALID_TYPE -> {
                msg = "Invalid"
                title = "Invalid file type"
            }
            DiffMessageType.DIFFERENT_TYPES -> {
                msg = "Select files with the same file type"
                title = "Different file types"
            }
        }
        Messages.showMessageDialog(msg, title, Messages.getWarningIcon())
    }
}
