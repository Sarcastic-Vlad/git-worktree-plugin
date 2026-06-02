package io.github.sarcasticvlad.worktree.model

data class Worktree(
    val path: String,
    val headSha: String?,
    val branch: String?,        // короткое имя (без refs/heads/); null если detached/bare
    val isBare: Boolean = false,
    val isDetached: Boolean = false,
    val isLocked: Boolean = false,
    val lockReason: String? = null,
    val isCurrent: Boolean = false,   // выставляется сервисом, не парсером
) {
    val displayName: String get() = path.trimEnd('/').substringAfterLast('/')
}
