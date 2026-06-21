package io.github.sarcasticvlad.worktree.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import io.github.sarcasticvlad.worktree.open.WorktreeOpener
import io.github.sarcasticvlad.worktree.ui.WorktreePanel

class OpenWorktreeAction(private val panel: WorktreePanel) :
    DumbAwareAction("Open", "Open selected worktree (IDE asks: this / new window)", AllIcons.Actions.MenuOpen) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val wt = panel.selectedWorktree() ?: return
        // newWindow=false → IDE shows its standard This Window / New Window chooser
        WorktreeOpener.open(wt.path, project, newWindow = false)
    }

    override fun update(e: AnActionEvent) {
        val wt = panel.selectedWorktree()
        e.presentation.isEnabled = wt != null && !wt.isCurrent && !wt.isBare
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT
}
