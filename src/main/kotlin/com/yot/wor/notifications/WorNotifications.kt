package com.yot.wor.notifications

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.settings.WoRSettings

object WorNotifications {

    private const val NOTIFICATION_GROUP_ID = "WoR Notifications"

    fun notifyXPGain(project: Project, action: RefactoringAction) {
        val settings = WoRSettings.getInstance(project)
        if (!settings.showXPNotifications) return

        val title = "✨ ${action.type.gameplayTag}"
        val content = buildString {
            append("<b>${action.type.displayName}</b>")
            append(" <span style='color:#00FF00;'>+${action.xpReward} XP</span>")
            action.fileName?.let {
                append("<br/><i>$it</i>")
            }
        }

        showNotification(
            project = project,
            title = title,
            content = content,
            type = NotificationType.INFORMATION
        )
    }

    fun notifyLevelUp(project: Project, oldLevel: Int, newLevel: Int, title: String) {
        val settings = WoRSettings.getInstance(project)
        if (!settings.showLevelUpNotifications) return

        val content = buildString {
            append("🎉 <b>LEVEL UP!</b> ")
            append("$oldLevel → $newLevel<br/>")
            append("<i>$title</i>")
        }

        showNotification(
            project = project,
            title = "🎊 Congratulations !",
            content = content,
            type = NotificationType.INFORMATION,
            important = true
        )
    }

    fun notifyCombo(project: Project, comboName: String, multiplier: Double) {
        val settings = WoRSettings.getInstance(project)
        if (!settings.showComboNotifications) return

        val content = buildString {
            append("🔥 <b>COMBO!</b><br/>")
            append("$comboName<br/>")
            append("<span style='color:#FFD700;'>XP × ${multiplier}x</span>")
        }

        showNotification(
            project = project,
            title = "⚡ Combo!",
            content = content,
            type = NotificationType.INFORMATION
        )
    }

    fun notifyAchievement(project: Project, achievementName: String, description: String) {
        val settings = WoRSettings.getInstance(project)
        if (!settings.showAchievementNotifications) return

        val content = buildString {
            append("🏆 <b>$achievementName</b><br/>")
            append(description)
        }

        showNotification(
            project = project,
            title = "🎖️ Achievement Unlocked!",
            content = content,
            type = NotificationType.INFORMATION,
            important = true
        )
    }

    private fun showNotification(
        project: Project,
        title: String,
        content: String,
        type: NotificationType,
        important: Boolean = false
    ) {
        val notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)

        val notification = notificationGroup.createNotification(
            title = title,
            content = content,
            type = type
        )

        if (important) notification.isImportant = true

        notification.notify(project)
    }
}
