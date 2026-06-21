package io.github.sarcasticvlad.worktree.ui

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object WorktreeNotifier {
    fun error(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Git Worktree")
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }
}
