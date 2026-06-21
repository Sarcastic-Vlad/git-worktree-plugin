package io.github.sarcasticvlad.worktree.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import git4idea.commands.GitCommandResult
import io.github.sarcasticvlad.worktree.git.WorktreeGitService
import io.github.sarcasticvlad.worktree.git.WorktreeSource
import io.github.sarcasticvlad.worktree.ui.AddWorktreeDialog
import io.github.sarcasticvlad.worktree.ui.WorktreeNotifier
import io.github.sarcasticvlad.worktree.ui.WorktreePanel

class AddWorktreeAction(private val panel: WorktreePanel) :
    DumbAwareAction("Add", "Create a new worktree", AllIcons.General.Add) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val service = WorktreeGitService.getInstance(project)

        val branches = service.localBranchNames()
        val current = service.currentBranchName()
        val parent = service.repoParentPath() ?: return
        val repoName = service.repoName() ?: "repo"
        val defaultPath = "$parent/$repoName-worktree"

        val dialog = AddWorktreeDialog(project, branches, current, defaultPath)
        if (!dialog.showAndGet()) return

        val path = dialog.selectedPath()
        val source: WorktreeSource = dialog.selectedSource()

        object : Task.Backgroundable(project, "Adding worktree", false) {
            private lateinit var result: GitCommandResult
            override fun run(indicator: ProgressIndicator) {
                result = service.add(path, source)
            }
            override fun onSuccess() {
                if (result.success()) {
                    panel.refresh()
                } else {
                    WorktreeNotifier.error(project, "Failed to add worktree: ${result.errorOutputAsJoinedString}")
                }
            }
        }.queue()
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
