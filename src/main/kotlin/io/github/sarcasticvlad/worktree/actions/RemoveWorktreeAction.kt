package io.github.sarcasticvlad.worktree.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import git4idea.commands.GitCommandResult
import io.github.sarcasticvlad.worktree.git.WorktreeGitService
import io.github.sarcasticvlad.worktree.model.Worktree
import io.github.sarcasticvlad.worktree.ui.WorktreeNotifier
import io.github.sarcasticvlad.worktree.ui.WorktreePanel

class RemoveWorktreeAction(private val panel: WorktreePanel) :
    DumbAwareAction("Remove", "Remove selected worktree", AllIcons.General.Remove) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val wt = panel.selectedWorktree() ?: return

        val confirm = Messages.showYesNoDialog(
            project,
            "Remove worktree at:\n${wt.path}?",
            "Remove Worktree",
            Messages.getQuestionIcon(),
        )
        if (confirm != Messages.YES) return

        removeWithForceFallback(project, wt, force = false)
    }

    private fun removeWithForceFallback(project: Project, wt: Worktree, force: Boolean) {
        val service = WorktreeGitService.getInstance(project)
        object : Task.Backgroundable(project, "Removing worktree", false) {
            private lateinit var result: GitCommandResult
            override fun run(indicator: ProgressIndicator) {
                result = service.remove(wt.path, force)
            }
            override fun onSuccess() {
                when {
                    result.success() -> panel.refresh()
                    !force -> promptForce(project, wt, result)
                    else -> WorktreeNotifier.error(
                        project, "Failed to remove worktree: ${result.errorOutputAsJoinedString}",
                    )
                }
            }
        }.queue()
    }

    private fun promptForce(project: Project, wt: Worktree, result: GitCommandResult) {
        ApplicationManager.getApplication().invokeLater {
            val answer = Messages.showYesNoDialog(
                project,
                "Git refused to remove the worktree:\n${result.errorOutputAsJoinedString}\n\nForce remove?",
                "Force Remove Worktree",
                Messages.getWarningIcon(),
            )
            if (answer == Messages.YES) {
                removeWithForceFallback(project, wt, force = true)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val wt = panel.selectedWorktree()
        e.presentation.isEnabled = wt != null && !wt.isCurrent && !wt.isBare
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT
}
