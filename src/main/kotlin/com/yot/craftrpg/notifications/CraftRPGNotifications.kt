package com.yot.craftrpg.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.yot.craftrpg.domain.RefactoringAction
import com.yot.craftrpg.settings.CraftRPGSettings

/**
 * Service de notifications pour Craft RPG
 */
object CraftRPGNotifications {

    private const val NOTIFICATION_GROUP_ID = "Craft RPG Notifications"

    /**
     * Notifie le joueur d'un gain d'XP
     */
    fun notifyXPGain(project: Project, action: RefactoringAction) {
        val settings = CraftRPGSettings.getInstance(project)
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

    /**
     * Notifie le joueur d'un level up
     */
    fun notifyLevelUp(project: Project, oldLevel: Int, newLevel: Int, title: String) {
        val settings = CraftRPGSettings.getInstance(project)
        if (!settings.showLevelUpNotifications) return

        val content = buildString {
            append("🎉 <b>LEVEL UP!</b> ")
            append("$oldLevel → $newLevel<br/>")
            append("<i>$title</i>")
        }

        showNotification(
            project = project,
            title = "🎊 Félicitations !",
            content = content,
            type = NotificationType.INFORMATION,
            important = true
        )
    }

    /**
     * Notifie d'un combo de refactorings
     */
    fun notifyCombo(project: Project, comboName: String, multiplier: Double) {
        val settings = CraftRPGSettings.getInstance(project)
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

    /**
     * Notifie d'un achievement débloqué
     */
    fun notifyAchievement(project: Project, achievementName: String, description: String) {
        val settings = CraftRPGSettings.getInstance(project)
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

        if (important) {
            notification.isImportant = true
        }

        notification.notify(project)
    }
}
