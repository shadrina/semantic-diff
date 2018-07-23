package ru.nsu.diff.view

import com.intellij.openapi.ui.Messages

const val NO_FILES_MSG_CODE = 0
const val DIFFERENT_TYPES_MSG_CODE = 1

class DiffViewerNotifier {
    companion object {
        fun showDialog(msgCode: Int) {
            var msg = ""
            var title = ""

            when (msgCode) {
                NO_FILES_MSG_CODE -> {
                    msg = "Specify both files to view diff"
                    title = "Select files"
                }
                DIFFERENT_TYPES_MSG_CODE -> {
                    msg = "Select files with the same file type"
                    title = "Different file types"
                }
            }
            Messages.showMessageDialog(msg, title, Messages.getWarningIcon())
        }
    }
}