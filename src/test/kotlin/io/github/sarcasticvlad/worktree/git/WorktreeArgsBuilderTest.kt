package io.github.sarcasticvlad.worktree.git

import org.junit.Assert.assertEquals
import org.junit.Test

class WorktreeArgsBuilderTest {

    @Test
    fun `add from existing branch`() {
        val args = WorktreeArgsBuilder.buildAddArgs(
            "/tmp/wt", WorktreeSource.ExistingBranch("feature/x"),
        )
        assertEquals(listOf("add", "/tmp/wt", "feature/x"), args)
    }

    @Test
    fun `add with new branch`() {
        val args = WorktreeArgsBuilder.buildAddArgs(
            "/tmp/wt", WorktreeSource.NewBranch("feature/new", "main"),
        )
        assertEquals(listOf("add", "-b", "feature/new", "/tmp/wt", "main"), args)
    }

    @Test
    fun `remove without force`() {
        assertEquals(listOf("remove", "/tmp/wt"), WorktreeArgsBuilder.buildRemoveArgs("/tmp/wt", false))
    }

    @Test
    fun `remove with force`() {
        assertEquals(listOf("remove", "--force", "/tmp/wt"), WorktreeArgsBuilder.buildRemoveArgs("/tmp/wt", true))
    }
}
