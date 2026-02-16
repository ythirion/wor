package com.yot.wor.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.yot.wor.domain.*
import com.yot.wor.notifications.WorNotifications
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

@Service(Service.Level.APP)
@State(
    name = "WoRPlayerState",
    storages = [Storage("wor-player.xml")]
)
class PlayerStateService : PersistentStateComponent<PlayerStateService.State> {

    private val listeners = CopyOnWriteArrayList<PlayerStateListener>()
    private var currentState = State()

    data class State(
        var totalXP: Int = 0,
        var actionsHistory: MutableList<PersistedAction> = mutableListOf()
    )

    data class PersistedAction(
        var actionTypeId: String = "",
        var timestamp: Long = 0,
        var fileName: String? = null,
        var elementName: String? = null
    )

    fun interface PlayerStateListener {
        fun onStateChanged(state: PlayerState)
    }

    override fun getState(): State = currentState

    override fun loadState(state: State) {
        currentState = state
        thisLogger().info("Player state loaded: ${state.totalXP} XP, ${state.actionsHistory.size} actions")
    }

    fun addRefactoringAction(action: RefactoringAction, project: Project) {
        val persistedAction = PersistedAction(
            actionTypeId = action.type.name,
            timestamp = action.timestamp.toEpochMilli(),
            fileName = action.fileName,
            elementName = action.elementName
        )

        currentState.actionsHistory.add(persistedAction)
        val oldTotalXP = currentState.totalXP
        currentState.totalXP += action.xpReward

        val oldLevel = PlayerState.calculateLevel(oldTotalXP)
        val newLevel = PlayerState.calculateLevel(currentState.totalXP)

        WorNotifications.notifyXPGain(project, action)

        if (newLevel > oldLevel) {
            val playerState = playerState()
            thisLogger().info("🎉 Level up! $oldLevel → $newLevel - ${playerState.title}")
            WorNotifications.notifyLevelUp(project, oldLevel, newLevel, playerState.title)
        }

        notifyListeners()
    }

    fun addQuestXP(xp: Int, project: Project? = null) {
        val oldTotalXP = currentState.totalXP
        currentState.totalXP += xp

        val oldLevel = PlayerState.calculateLevel(oldTotalXP)
        val newLevel = PlayerState.calculateLevel(currentState.totalXP)

        thisLogger().info("Quest XP gained: +$xp XP")

        if (newLevel > oldLevel && project != null) {
            val playerState = playerState()
            thisLogger().info("🎉 Level up! $oldLevel → $newLevel - ${playerState.title}")
            WorNotifications.notifyLevelUp(project, oldLevel, newLevel, playerState.title)
        }

        notifyListeners()
    }

    fun playerState(): PlayerState {
        val actions = currentState.actionsHistory.mapNotNull { persisted ->
            try {
                val type = RefactoringActionType.valueOf(persisted.actionTypeId)
                RefactoringAction(
                    type = type,
                    timestamp = Instant.ofEpochMilli(persisted.timestamp),
                    fileName = persisted.fileName,
                    elementName = persisted.elementName
                )
            } catch (_: IllegalArgumentException) {
                thisLogger().warn("Unknown action type: ${persisted.actionTypeId}")
                null
            }
        }

        val level = PlayerState.calculateLevel(currentState.totalXP)
        val statisticsByCategory = calculateCategoryStats(actions)
        val lastAction = actions.maxByOrNull { it.timestamp }

        return PlayerState(
            totalXP = currentState.totalXP,
            level = level,
            actionsHistory = actions,
            statisticsByCategory = statisticsByCategory,
            lastActionTimestamp = lastAction?.timestamp
        )
    }

    private fun calculateCategoryStats(actions: List<RefactoringAction>): Map<ActionCategory, CategoryStats> =
        ActionCategory.entries.associateWith { category ->
            val categoryActions = actions.filter { it.category == category }
            val actionCounts = categoryActions.groupingBy { it.type }.eachCount()
            val mostUsed = actionCounts.maxByOrNull { it.value }?.key

            CategoryStats(
                category = category,
                actionCount = categoryActions.size,
                totalXP = categoryActions.sumOf { it.xpReward },
                mostUsedAction = mostUsed
            )
        }

    fun addListener(listener: PlayerStateListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: PlayerStateListener) {
        listeners.remove(listener)
    }

    fun reset() {
        currentState.totalXP = 0
        currentState.actionsHistory.clear()
        notifyListeners()
    }

    private fun notifyListeners() =
        listeners.forEach { listener ->
            try {
                listener.onStateChanged(playerState())
            } catch (e: Exception) {
                thisLogger().error("Error notifying listener", e)
            }
        }

    companion object {
        fun getInstance(): PlayerStateService =
            ApplicationManager.getApplication().getService(PlayerStateService::class.java)
    }
}
