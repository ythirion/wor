package com.yot.craftrpg.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * Paramètres du plugin Craft RPG
 */
@Service(Service.Level.PROJECT)
@State(
    name = "CraftRPGSettings",
    storages = [Storage("craft-rpg-settings.xml")]
)
class CraftRPGSettings : PersistentStateComponent<CraftRPGSettings.State> {

    private var myState = State()

    data class State(
        var showXPNotifications: Boolean = true,
        var showLevelUpNotifications: Boolean = true,
        var showComboNotifications: Boolean = true,
        var showAchievementNotifications: Boolean = true,
        var notificationDuration: Int = 3000 // millisecondes
    )

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var showXPNotifications: Boolean
        get() = myState.showXPNotifications
        set(value) {
            myState.showXPNotifications = value
        }

    var showLevelUpNotifications: Boolean
        get() = myState.showLevelUpNotifications
        set(value) {
            myState.showLevelUpNotifications = value
        }

    var showComboNotifications: Boolean
        get() = myState.showComboNotifications
        set(value) {
            myState.showComboNotifications = value
        }

    var showAchievementNotifications: Boolean
        get() = myState.showAchievementNotifications
        set(value) {
            myState.showAchievementNotifications = value
        }

    var notificationDuration: Int
        get() = myState.notificationDuration
        set(value) {
            myState.notificationDuration = value
        }

    companion object {
        fun getInstance(project: Project): CraftRPGSettings =
            project.getService(CraftRPGSettings::class.java)
    }
}
