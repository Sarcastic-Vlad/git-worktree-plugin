package io.github.sarcasticvlad.worktree.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBList
import io.github.sarcasticvlad.worktree.actions.RefreshWorktreesAction
import io.github.sarcasticvlad.worktree.git.WorktreeGitService
import io.github.sarcasticvlad.worktree.model.Worktree
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.ListSelectionModel

class WorktreePanel(private val project: Project) : SimpleToolWindowPanel(true, true) {

    private val listModel = CollectionListModel<Worktree>()
    private val list = JBList(listModel).apply {
        cellRenderer = WorktreeListCellRenderer()
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        emptyText.text = "No worktrees"
    }
    private val refreshing = AtomicBoolean(false)

    init {
        val group = DefaultActionGroup().apply {
            add(RefreshWorktreesAction(this@WorktreePanel))
        }
        val toolbar = ActionManager.getInstance().createActionToolbar("WorktreePanel", group, true)
        toolbar.targetComponent = this
        setToolbar(toolbar.component)
        setContent(ScrollPaneFactory.createScrollPane(list))
        refresh()
    }

    fun selectedWorktree(): Worktree? = list.selectedValue

    fun refresh() {
        if (!refreshing.compareAndSet(false, true)) return
        object : Task.Backgroundable(project, "Loading worktrees", false) {
            private var loaded: List<Worktree> = emptyList()
            private var error: String? = null

            override fun run(indicator: ProgressIndicator) {
                WorktreeGitService.getInstance(project).list()
                    .onSuccess { loaded = it }
                    .onFailure { error = it.message }
            }

            override fun onSuccess() {
                listModel.replaceAll(loaded)
                list.emptyText.text = error ?: "No worktrees"
            }

            override fun onFinished() {
                refreshing.set(false)
            }
        }.queue()
    }
}
