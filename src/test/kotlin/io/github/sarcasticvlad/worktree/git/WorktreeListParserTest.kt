package io.github.sarcasticvlad.worktree.git

import io.github.sarcasticvlad.worktree.model.Worktree
import org.junit.Assert.assertEquals
import org.junit.Test

class WorktreeListParserTest {

    @Test
    fun `parses main and feature worktrees`() {
        val out = listOf(
            "worktree /home/u/repo",
            "HEAD abc123",
            "branch refs/heads/main",
            "",
            "worktree /home/u/repo-feature",
            "HEAD def456",
            "branch refs/heads/feature/x",
            "",
        )
        val result = WorktreeListParser.parse(out)
        assertEquals(
            listOf(
                Worktree("/home/u/repo", "abc123", "main"),
                Worktree("/home/u/repo-feature", "def456", "feature/x"),
            ),
            result,
        )
    }

    @Test
    fun `parses detached worktree`() {
        val out = listOf(
            "worktree /home/u/repo-det",
            "HEAD aaa111",
            "detached",
            "",
        )
        val result = WorktreeListParser.parse(out)
        assertEquals(
            listOf(Worktree("/home/u/repo-det", "aaa111", null, isDetached = true)),
            result,
        )
    }

    @Test
    fun `parses bare worktree`() {
        val out = listOf(
            "worktree /home/u/repo-bare",
            "bare",
            "",
        )
        val result = WorktreeListParser.parse(out)
        assertEquals(
            listOf(Worktree("/home/u/repo-bare", null, null, isBare = true)),
            result,
        )
    }

    @Test
    fun `parses locked worktree with reason`() {
        val out = listOf(
            "worktree /mnt/ext/repo-wt",
            "HEAD bbb222",
            "branch refs/heads/wip",
            "locked on removable drive",
            "",
        )
        val result = WorktreeListParser.parse(out)
        assertEquals(
            listOf(
                Worktree(
                    "/mnt/ext/repo-wt", "bbb222", "wip",
                    isLocked = true, lockReason = "on removable drive",
                ),
            ),
            result,
        )
    }

    @Test
    fun `handles trailing entry without blank line`() {
        val out = listOf(
            "worktree /home/u/repo",
            "HEAD abc123",
            "branch refs/heads/main",
        )
        val result = WorktreeListParser.parse(out)
        assertEquals(1, result.size)
        assertEquals("main", result[0].branch)
    }

    @Test
    fun `returns empty list for empty input`() {
        assertEquals(emptyList<Worktree>(), WorktreeListParser.parse(emptyList()))
    }
}
