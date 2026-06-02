package io.github.sarcasticvlad.worktree.open

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.project.Project
import java.nio.file.Path

object WorktreeOpener {
    /** Открыть worktree как проект. newWindow=true — в новом окне; false — IDE спросит (this/new). */
    fun open(path: String, projectToClose: Project?, newWindow: Boolean) {
        ProjectUtil.openOrImport(Path.of(path), projectToClose, newWindow)
    }
}
