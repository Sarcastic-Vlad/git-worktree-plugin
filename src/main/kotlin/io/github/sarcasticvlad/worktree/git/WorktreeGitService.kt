package io.github.sarcasticvlad.worktree.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import io.github.sarcasticvlad.worktree.model.Worktree

@Service(Service.Level.PROJECT)
class WorktreeGitService(private val project: Project) {

    /** Активный (первый) git-репозиторий проекта, либо null. */
    fun primaryRepository(): GitRepository? =
        GitRepositoryManager.getInstance(project).repositories.firstOrNull()

    fun list(): Result<List<Worktree>> {
        val repo = primaryRepository()
            ?: return Result.failure(IllegalStateException("No Git repository"))
        val handler = GitLineHandler(project, repo.root, GitCommand.WORKTREE)
        handler.addParameters("list", "--porcelain")
        val res = Git.getInstance().runCommand(handler)
        if (!res.success()) {
            return Result.failure(RuntimeException(res.errorOutputAsJoinedString))
        }
        val rootPath = repo.root.path.trimEnd('/')
        // Сравнение по строке пути; на symlink-путях (macOS /var → /private/var) может
        // не совпасть. Достаточно для MVP; TODO: сравнивать через VirtualFile/toRealPath().
        val worktrees = WorktreeListParser.parse(res.output)
            .map { it.copy(isCurrent = it.path.trimEnd('/') == rootPath) }
        return Result.success(worktrees)
    }

    companion object {
        fun getInstance(project: Project): WorktreeGitService = project.service()
    }
}
