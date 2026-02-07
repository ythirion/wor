package com.yot.craftrpg.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.yot.craftrpg.domain.*
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Service pour gérer l'état du joueur
 * Persiste automatiquement les données entre les sessions
 */
@Service(Service.Level.PROJECT)
@State(
    name = "CraftRPGPlayerState",
    storages = [Storage("craft-rpg-player.xml")]
)
class PlayerStateService(
    private val project: Project
) : PersistentStateComponent<PlayerStateService.State> {

    private val listeners = CopyOnWriteArrayList<PlayerStateListener>()
    private var currentState = State()

    /**
     * État persisté
     */
    data class State(
        var totalXP: Int = 0,
        var actionsHistory: MutableList<PersistedAction> = mutableListOf()
    )

    /**
     * Action persistée (version simplifiée pour la sérialisation)
     */
    data class PersistedAction(
        var actionTypeId: String = "",
        var timestamp: Long = 0,
        var fileName: String? = null,
        var elementName: String? = null
    )

    /**
     * Interface pour écouter les changements d'état
     */
    fun interface PlayerStateListener {
        fun onStateChanged(state: PlayerState)
    }

    init {
        // S'abonner aux actions de refactoring
        RefactoringDetectionService.getInstance(project).addListener { action ->
            addRefactoringAction(action)
        }
    }

    override fun getState(): State = currentState

    override fun loadState(state: State) {
        currentState = state
        thisLogger().info("Player state loaded: ${state.totalXP} XP, ${state.actionsHistory.size} actions")
    }

    /**
     * Ajoute une action de refactoring et met à jour l'XP
     */
    fun addRefactoringAction(action: RefactoringAction) {
        val persistedAction = PersistedAction(
            actionTypeId = action.type.name,
            timestamp = action.timestamp.toEpochMilli(),
            fileName = action.fileName,
            elementName = action.elementName
        )

        currentState.actionsHistory.add(persistedAction)
        currentState.totalXP += action.xpReward

        val oldLevel = PlayerState.calculateLevel(currentState.totalXP - action.xpReward)
        val newLevel = PlayerState.calculateLevel(currentState.totalXP)

        if (newLevel > oldLevel) {
            thisLogger().info("🎉 Level up! ${oldLevel} → ${newLevel}")
            // TODO: déclencher une notification de level up
        }

        notifyListeners()
    }

    /**
     * Récupère l'état actuel du joueur
     */
    fun getPlayerState(): PlayerState {
        val actions = currentState.actionsHistory.mapNotNull { persisted ->
            try {
                val type = RefactoringActionType.valueOf(persisted.actionTypeId)
                RefactoringAction(
                    type = type,
                    timestamp = Instant.ofEpochMilli(persisted.timestamp),
                    fileName = persisted.fileName,
                    elementName = persisted.elementName
                )
            } catch (e: IllegalArgumentException) {
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

    /**
     * Calcule les statistiques par catégorie
     */
    private fun calculateCategoryStats(actions: List<RefactoringAction>): Map<ActionCategory, CategoryStats> {
        return ActionCategory.entries.associateWith { category ->
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
    }

    /**
     * Ajoute un listener
     */
    fun addListener(listener: PlayerStateListener) {
        listeners.add(listener)
    }

    /**
     * Retire un listener
     */
    fun removeListener(listener: PlayerStateListener) {
        listeners.remove(listener)
    }

    /**
     * Réinitialise l'état (pour les tests ou debug)
     */
    fun reset() {
        currentState.totalXP = 0
        currentState.actionsHistory.clear()
        notifyListeners()
    }

    private fun notifyListeners() {
        val state = getPlayerState()
        listeners.forEach { listener ->
            try {
                listener.onStateChanged(state)
            } catch (e: Exception) {
                thisLogger().error("Error notifying listener", e)
            }
        }
    }

    companion object {
        fun getInstance(project: Project): PlayerStateService =
            project.getService(PlayerStateService::class.java)
    }
}
