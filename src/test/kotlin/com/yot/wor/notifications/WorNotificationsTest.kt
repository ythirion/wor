package com.yot.wor.notifications

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class WorNotificationsTest : BasePlatformTestCase() {

    private val notifications = mutableListOf<Notification>()

    override fun setUp() {
        super.setUp()
        notifications.clear()

        project.messageBus.connect(testRootDisposable).subscribe(
            Notifications.TOPIC,
            object : Notifications {
                override fun notify(notification: Notification) {
                    notifications.add(notification)
                }
            }
        )
    }

    fun `test notifyXPGain should create notification with correct content`() {
        val action = RefactoringAction(
            type = RefactoringActionType.EXTRACT_METHOD,
            fileName = "Test.kt"
        )

        WorNotifications.notifyXPGain(project, action)

        notifications.size shouldBe 1
        val notification = notifications.first()

        notification.title shouldContain "🧪 Clarity"
        notification.content shouldContain "Extract Method"
        notification.content shouldContain "+10 XP"
        notification.content shouldContain "Test.kt"
    }

    fun `test notifyXPGain without fileName should not crash`() {
        val action = RefactoringAction(
            type = RefactoringActionType.RENAME
        )

        WorNotifications.notifyXPGain(project, action)

        notifications.size shouldBe 1
        val notification = notifications.first()

        notification.title shouldContain "✨ Clarity"
        notification.content shouldContain "Rename"
        notification.content shouldContain "+5 XP"
    }

    fun `test notifyLevelUp should create important notification`() {
        WorNotifications.notifyLevelUp(
            project = project,
            oldLevel = 1,
            newLevel = 2,
            title = "🌱 Apprenti Refactorer"
        )

        notifications.size shouldBe 1
        val notification = notifications.first()

        notification.title shouldContain "Félicitations"
        notification.content shouldContain "LEVEL UP"
        notification.content shouldContain "1 → 2"
        notification.content shouldContain "Apprenti Refactorer"
        notification.isImportant shouldBe true
    }

    fun `test notifyCombo should create notification with multiplier`() {
        WorNotifications.notifyCombo(
            project = project,
            comboName = "Rename → Extract → Move",
            multiplier = 1.5
        )

        notifications.size shouldBe 1
        val notification = notifications.first()

        notification.title shouldContain "Combo"
        notification.content shouldContain "COMBO"
        notification.content shouldContain "Rename → Extract → Move"
        notification.content shouldContain "1.5x"
    }

    fun `test notifyAchievement should create important notification`() {
        WorNotifications.notifyAchievement(
            project = project,
            achievementName = "First Blood",
            description = "Perform your first refactoring"
        )

        notifications.size shouldBe 1
        val notification = notifications.first()

        notification.title shouldContain "Achievement"
        notification.content shouldContain "First Blood"
        notification.content shouldContain "Perform your first refactoring"
        notification.isImportant shouldBe true
    }

    fun `test multiple notifications should all be created`() {
        val action1 = RefactoringAction(type = RefactoringActionType.RENAME)
        val action2 = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)

        WorNotifications.notifyXPGain(project, action1)
        WorNotifications.notifyXPGain(project, action2)

        notifications.size shouldBe 2
        notifications[0].content shouldContain "Rename"
        notifications[1].content shouldContain "Extract Method"
    }

    fun `test different action types should have different gameplay tags`() {
        val renameAction = RefactoringAction(type = RefactoringActionType.RENAME)
        WorNotifications.notifyXPGain(project, renameAction)

        val extractAction = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
        WorNotifications.notifyXPGain(project, extractAction)

        notifications.size shouldBe 2
        notifications[0].title shouldNotBe notifications[1].title
    }
}
