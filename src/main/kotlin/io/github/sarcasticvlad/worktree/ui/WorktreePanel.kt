package io.github.sarcasticvlad.worktree.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import javax.swing.JLabel

class WorktreePanel(private val project: Project) : SimpleToolWindowPanel(true, true) {
    init {
        setContent(JLabel("Worktrees panel — coming soon"))
    }
}
