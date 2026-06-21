package io.github.sarcasticvlad.worktree.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import io.github.sarcasticvlad.worktree.ui.WorktreePanel

class RefreshWorktreesAction(private val panel: WorktreePanel) :
    DumbAwareAction("Refresh", "Reload worktrees", AllIcons.Actions.Refresh) {
    override fun actionPerformed(e: AnActionEvent) = panel.refresh()
    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
