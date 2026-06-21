package io.github.sarcasticvlad.worktree.git

sealed interface WorktreeSource {
    data class ExistingBranch(val branch: String) : WorktreeSource
    data class NewBranch(val name: String, val startPoint: String) : WorktreeSource
}

object WorktreeArgsBuilder {

    fun buildAddArgs(path: String, source: WorktreeSource): List<String> = when (source) {
        is WorktreeSource.ExistingBranch -> listOf("add", path, source.branch)
        is WorktreeSource.NewBranch -> listOf("add", "-b", source.name, path, source.startPoint)
    }

    fun buildRemoveArgs(path: String, force: Boolean): List<String> = buildList {
        add("remove")
        if (force) add("--force")
        add(path)
    }
}
