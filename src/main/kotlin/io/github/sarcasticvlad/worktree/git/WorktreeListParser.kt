package io.github.sarcasticvlad.worktree.git

import io.github.sarcasticvlad.worktree.model.Worktree

/** Парсит вывод `git worktree list --porcelain`. */
object WorktreeListParser {

    fun parse(porcelainLines: List<String>): List<Worktree> {
        val result = mutableListOf<Worktree>()

        var path: String? = null
        var head: String? = null
        var branch: String? = null
        var bare = false
        var detached = false
        var locked = false
        var lockReason: String? = null

        fun flush() {
            val p = path ?: return
            result += Worktree(
                path = p,
                headSha = head,
                branch = branch,
                isBare = bare,
                isDetached = detached,
                isLocked = locked,
                lockReason = lockReason,
            )
        }

        fun reset() {
            path = null; head = null; branch = null
            bare = false; detached = false; locked = false; lockReason = null
        }

        for (raw in porcelainLines) {
            val line = raw.trimEnd()
            when {
                line.startsWith("worktree ") -> {
                    flush()
                    reset()
                    path = line.removePrefix("worktree ")
                }
                line.startsWith("HEAD ") -> head = line.removePrefix("HEAD ")
                line.startsWith("branch ") ->
                    branch = line.removePrefix("branch ").removePrefix("refs/heads/")
                line == "bare" -> bare = true
                line == "detached" -> detached = true
                line == "locked" -> locked = true
                line.startsWith("locked ") -> {
                    locked = true
                    lockReason = line.removePrefix("locked ")
                }
            }
        }
        flush()
        return result
    }
}
