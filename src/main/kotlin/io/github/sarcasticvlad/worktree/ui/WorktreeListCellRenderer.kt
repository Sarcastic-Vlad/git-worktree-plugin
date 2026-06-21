package io.github.sarcasticvlad.worktree.ui

import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import io.github.sarcasticvlad.worktree.model.Worktree
import javax.swing.JList

class WorktreeListCellRenderer : ColoredListCellRenderer<Worktree>() {
    override fun customizeCellRenderer(
        list: JList<out Worktree>,
        value: Worktree,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean,
    ) {
        val name = value.branch ?: value.headSha?.take(7) ?: value.displayName
        append(name, SimpleTextAttributes.REGULAR_ATTRIBUTES)

        val tags = buildList {
            if (value.isCurrent) add("current")
            if (value.isBare) add("bare")
            if (value.isDetached) add("detached")
            if (value.isLocked) add("locked")
        }
        if (tags.isNotEmpty()) {
            append("  [${tags.joinToString(", ")}]", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
        append("  ${value.path}", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
    }
}
