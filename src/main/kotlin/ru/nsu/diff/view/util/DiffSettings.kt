package ru.nsu.diff.view.util

/*******************************************************  *  *  *
 *        12%        *        60%        *     28%     *   18%  *
 *  NavigationPanel  *  ChangeablePanel  *  InfoPanel  *        *
 *******************************************************  *  *  *
 *                         *                           *        *
 *                   DiffViewerPanel                   *   60%  *
 *                         *                           *        *
 *******************************************************  *  *  *
 *                                      * Diff * Close *        *
 *******************************************************  *  *  */

const val DIFF_DIALOG_WIDTH = 1570
const val Diff_DIALOG_HEIGHT = 600

const val DIFF_VIEWER_WIDTH = (DIFF_DIALOG_WIDTH * 0.985).toInt()
const val DIFF_VIEWER_HEIGHT = (Diff_DIALOG_HEIGHT * 0.6).toInt()
const val DIVIDER_WIDTH = 30

const val DIFF_EDITOR_WIDTH = DIFF_VIEWER_WIDTH / 2 - DIVIDER_WIDTH / 2
const val DIFF_EDITOR_HEIGHT = DIFF_VIEWER_HEIGHT

const val UPPER_PANEL_WIDTH = DIFF_DIALOG_WIDTH
const val UPPER_PANEL_HEIGHT = (Diff_DIALOG_HEIGHT * 0.18).toInt()

const val NAVIGATION_PANEL_WIDTH = (DIFF_DIALOG_WIDTH * 0.12).toInt()
const val CHANGEABLE_PANEL_WIDTH = (DIFF_DIALOG_WIDTH * 0.6).toInt()
const val INFO_PANEL_WIDTH = (DIFF_DIALOG_WIDTH * 0.28).toInt()