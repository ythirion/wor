package com.yot.wor.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "WorSettings",
    storages = [Storage("wor-settings.xml")]
)
class WoRSettings : PersistentStateComponent<WoRSettings.State> {
    private var worState = State()

    data class State(
        var showXPNotifications: Boolean = true,
        var showLevelUpNotifications: Boolean = true,
        var showComboNotifications: Boolean = true,
        var showAchievementNotifications: Boolean = true,
        var notificationDuration: Int = 3000
    )

    override fun getState(): State = worState
    override fun loadState(state: State) {
        worState = state
    }

    var showXPNotifications: Boolean
        get() = worState.showXPNotifications
        set(value) {
            worState.showXPNotifications = value
        }

    var showLevelUpNotifications: Boolean
        get() = worState.showLevelUpNotifications
        set(value) {
            worState.showLevelUpNotifications = value
        }

    var showComboNotifications: Boolean
        get() = worState.showComboNotifications
        set(value) {
            worState.showComboNotifications = value
        }

    var showAchievementNotifications: Boolean
        get() = worState.showAchievementNotifications
        set(value) {
            worState.showAchievementNotifications = value
        }

    var notificationDuration: Int
        get() = worState.notificationDuration
        set(value) {
            worState.notificationDuration = value
        }

    companion object {
        fun getInstance(project: Project): WoRSettings =
            project.getService(WoRSettings::class.java)
    }
}
