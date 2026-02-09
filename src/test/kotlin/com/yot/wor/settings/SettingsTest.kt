package com.yot.wor.settings

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe

class SettingsTest : BasePlatformTestCase() {

    private lateinit var settings: WoRSettings

    override fun setUp() {
        super.setUp()
        settings = WoRSettings.getInstance(project)
    }

    fun `test default settings should enable all notifications`() {
        settings.showXPNotifications shouldBe true
        settings.showLevelUpNotifications shouldBe true
        settings.showComboNotifications shouldBe true
        settings.showAchievementNotifications shouldBe true
    }

    fun `test should be able to disable XP notifications`() {
        settings.showXPNotifications = false
        settings.showXPNotifications shouldBe false
    }

    fun `test should be able to disable level up notifications`() {
        settings.showLevelUpNotifications = false
        settings.showLevelUpNotifications shouldBe false
    }

    fun `test should be able to disable combo notifications`() {
        settings.showComboNotifications = false
        settings.showComboNotifications shouldBe false
    }

    fun `test should be able to disable achievement notifications`() {
        settings.showAchievementNotifications = false
        settings.showAchievementNotifications shouldBe false
    }

    fun `test should have default notification duration`() {
        settings.notificationDuration shouldBe 3000
    }

    fun `test should be able to change notification duration`() {
        settings.notificationDuration = 5000
        settings.notificationDuration shouldBe 5000
    }

    fun `test getInstance should return same instance`() {
        val settings1 = WoRSettings.getInstance(project)
        val settings2 = WoRSettings.getInstance(project)

        settings1 shouldBe settings2
    }

    fun `test state should be persisted`() {
        settings.showXPNotifications = false
        settings.showLevelUpNotifications = false
        settings.notificationDuration = 10000

        val state = settings.state
        state shouldBe WoRSettings.State(
            showXPNotifications = false,
            showLevelUpNotifications = false,
            showComboNotifications = true,
            showAchievementNotifications = true,
            notificationDuration = 10000
        )
    }

    fun `test should load state correctly`() {
        val newState = WoRSettings.State(
            showXPNotifications = false,
            showLevelUpNotifications = true,
            showComboNotifications = false,
            showAchievementNotifications = true,
            notificationDuration = 7000
        )

        settings.loadState(newState)

        settings.showXPNotifications shouldBe false
        settings.showLevelUpNotifications shouldBe true
        settings.showComboNotifications shouldBe false
        settings.showAchievementNotifications shouldBe true
        settings.notificationDuration shouldBe 7000
    }
}
