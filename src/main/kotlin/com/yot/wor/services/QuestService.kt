package com.yot.wor.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.thisLogger
import com.yot.wor.domain.*
import java.time.Instant
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Service(Service.Level.APP)
@State(
    name = "WoRQuestState",
    storages = [Storage("wor-quests.xml")]
)
class QuestService : PersistentStateComponent<QuestService.State> {

    private val activeQuests = CopyOnWriteArrayList<Quest>()
    private val completedQuests = CopyOnWriteArrayList<Quest>()
    private val listeners = CopyOnWriteArrayList<QuestListener>()

    data class State(
        var activeQuests: MutableList<PersistedQuest> = mutableListOf(),
        var completedQuests: MutableList<PersistedQuest> = mutableListOf()
    )

    data class PersistedQuest(
        var id: String = "",
        var title: String = "",
        var description: String = "",
        var category: String = "",
        var xpReward: Int = 0,
        var difficulty: String = "",
        var status: String = "",
        var objectives: MutableList<PersistedObjective> = mutableListOf(),
        var completedAt: Long? = null
    )

    data class PersistedObjective(
        var description: String = "",
        var targetCount: Int = 0,
        var currentCount: Int = 0
    )

    fun interface QuestListener {
        fun onQuestsChanged()
    }

    init {
        if (activeQuests.isEmpty() && completedQuests.isEmpty()) {
            generateStarterQuests()
        }
    }

    override fun getState(): State {
        return State(
            activeQuests = activeQuests.map { it.toPersisted() }.toMutableList(),
            completedQuests = completedQuests.map { it.toPersisted() }.toMutableList()
        )
    }

    override fun loadState(state: State) {
        activeQuests.clear()
        completedQuests.clear()

        activeQuests.addAll(state.activeQuests.map { it.toQuest() })
        completedQuests.addAll(state.completedQuests.map { it.toQuest() })

        // If no quests were loaded, generate starter quests
        if (activeQuests.isEmpty() && completedQuests.isEmpty()) {
            generateStarterQuests()
        }

        thisLogger().info("Quest state loaded: ${activeQuests.size} active, ${completedQuests.size} completed")
    }

    private fun Quest.toPersisted() = PersistedQuest(
        id = id,
        title = title,
        description = description,
        category = category.name,
        xpReward = xpReward,
        difficulty = difficulty.name,
        status = status.name,
        objectives = objectives.map {
            PersistedObjective(
                description = it.description,
                targetCount = it.targetCount,
                currentCount = it.currentCount
            )
        }.toMutableList(),
        completedAt = completedAt?.toEpochMilli()
    )

    private fun PersistedQuest.toQuest() = Quest(
        id = id,
        title = title,
        description = description,
        category = QuestCategory.valueOf(category),
        xpReward = xpReward,
        difficulty = QuestDifficulty.valueOf(difficulty),
        status = QuestStatus.valueOf(status),
        objectives = objectives.map {
            QuestObjective(
                description = it.description,
                targetCount = it.targetCount,
                currentCount = it.currentCount
            )
        },
        completedAt = completedAt?.let { Instant.ofEpochMilli(it) }
    )

    private fun generateStarterQuests() {
        addQuest(
            Quest(
                id = UUID.randomUUID().toString(),
                title = "First Steps",
                description = "Perform 5 refactoring actions to start your adventure",
                category = QuestCategory.REFACTORING,
                xpReward = 100,
                difficulty = QuestDifficulty.EASY,
                objectives = listOf(
                    QuestObjective("Perform 5 refactorings", targetCount = 5)
                )
            )
        )

        // Rename quest
        addQuest(
            Quest(
                id = UUID.randomUUID().toString(),
                title = "Rename Master",
                description = "Use the Rename action 10 times to clarify your code",
                category = QuestCategory.REFACTORING,
                xpReward = 150,
                difficulty = QuestDifficulty.EASY,
                objectives = listOf(
                    QuestObjective("Rename 10 elements", targetCount = 10)
                )
            )
        )

        // Extract quest
        addQuest(
            Quest(
                id = UUID.randomUUID().toString(),
                title = "Extract Expert",
                description = "Extract 5 methods to improve readability",
                category = QuestCategory.REFACTORING,
                xpReward = 200,
                difficulty = QuestDifficulty.MEDIUM,
                objectives = listOf(
                    QuestObjective("Extract Method × 5", targetCount = 5)
                )
            )
        )

        // Cleanup quest
        addQuest(
            Quest(
                id = UUID.randomUUID().toString(),
                title = "Spring Cleaning",
                description = "Clean your code by removing dead code and safely deleting unused elements",
                category = QuestCategory.CLEANUP,
                xpReward = 250,
                difficulty = QuestDifficulty.MEDIUM,
                objectives = listOf(
                    QuestObjective("Remove Dead Code × 5", targetCount = 5),
                    QuestObjective("Safe Delete × 5", targetCount = 5)
                )
            )
        )

        // Architecture quest
        addQuest(
            Quest(
                id = UUID.randomUUID().toString(),
                title = "Budding Architect",
                description = "Improve your code structure with advanced refactorings",
                category = QuestCategory.DESIGN,
                xpReward = 500,
                difficulty = QuestDifficulty.HARD,
                objectives = listOf(
                    QuestObjective("Move Class × 3", targetCount = 3),
                    QuestObjective("Move Method × 5", targetCount = 5)
                )
            )
        )
    }

    fun updateQuestProgress(action: RefactoringAction) {
        var questsUpdated = false
        thisLogger().info("=== Quest Update: ${action.type.displayName} ===")

        activeQuests
            .filter { quest -> quest.isAvailable }
            .forEach { quest ->
                thisLogger().info("Checking quest: ${quest.title} (status=${quest.status})")

                val updatedObjectives = quest.objectives.mapIndexed { index, objective ->
                    val shouldUpdate = shouldUpdateObjective(objective, action)
                    thisLogger().info("  Objective $index: '${objective.description}' - ${objective.currentCount}/${objective.targetCount} - shouldUpdate=$shouldUpdate - isCompleted=${objective.isCompleted}")

                    if (shouldUpdate) {
                        val updated = objective.copy(currentCount = objective.currentCount + 1)
                        thisLogger().info("  -> Updated to: ${updated.currentCount}/${updated.targetCount} - isCompleted=${updated.isCompleted}")
                        updated
                    } else objective
                }

                val allCompleted = updatedObjectives.all { it.isCompleted }
                thisLogger().info("  All objectives completed: $allCompleted")

                // Update status: COMPLETED if all objectives done, IN_PROGRESS if any progress made
                val newStatus = when {
                    allCompleted -> QuestStatus.COMPLETED
                    updatedObjectives.any { it.currentCount > 0 } && quest.status == QuestStatus.AVAILABLE -> QuestStatus.IN_PROGRESS
                    else -> quest.status
                }
                thisLogger().info("  New status: $newStatus")

                val updatedQuest = quest.copy(
                    objectives = updatedObjectives,
                    status = newStatus
                )

                thisLogger().info("  updatedQuest.isCompleted: ${updatedQuest.isCompleted}")

                if (updatedQuest.isCompleted) {
                    thisLogger().info("  ✅ COMPLETING QUEST: ${quest.title}")
                    activeQuests.remove(quest)  // Remove the original quest first
                    completeQuest(updatedQuest)
                    questsUpdated = true
                } else if (updatedQuest != quest) {
                    val index = activeQuests.indexOf(quest)

                    if (index >= 0) {
                        activeQuests[index] = updatedQuest
                        questsUpdated = true
                        thisLogger().info("  Quest updated in list")
                    }
                }
            }

        if (questsUpdated) notifyListeners()
    }

    private fun shouldUpdateObjective(objective: QuestObjective, action: RefactoringAction): Boolean {
        return when {
            objective.description.contains("refactoring", ignoreCase = true) -> true
            objective.description.contains(
                "Rename",
                ignoreCase = true
            ) && action.type == RefactoringActionType.RENAME -> true

            objective.description.contains(
                "Extract Method",
                ignoreCase = true
            ) && action.type == RefactoringActionType.EXTRACT_METHOD -> true

            objective.description.contains(
                "Move Method",
                ignoreCase = true
            ) && action.type == RefactoringActionType.MOVE_METHOD -> true

            objective.description.contains("Move Class", ignoreCase = true)
                && action.type == RefactoringActionType.MOVE_CLASS -> true

            objective.description.contains("Remove Dead Code", ignoreCase = true)
                && action.type == RefactoringActionType.REMOVE_DEAD_CODE -> true

            objective.description.contains("Safe Delete", ignoreCase = true)
                && action.type == RefactoringActionType.SAFE_DELETE -> true

            else -> false
        }
    }

    private fun completeQuest(quest: Quest) {
        val completedQuest = quest.copy(
            status = QuestStatus.COMPLETED,
            completedAt = Instant.now()
        )

        completedQuests.add(completedQuest)

        val questXP = (quest.xpReward * quest.difficulty.xpMultiplier).toInt()
        thisLogger().info("Quest completed: ${quest.title} (+$questXP XP)")

        // Add quest XP to player's total XP
        PlayerStateService.getInstance().addQuestXP(questXP)
    }

    fun addQuest(quest: Quest) {
        activeQuests.add(quest)
        notifyListeners()
    }

    fun activeQuests(): List<Quest> = activeQuests.toList()
    fun completedQuests(): List<Quest> = completedQuests.toList()
    fun questsByCategory(category: QuestCategory): List<Quest> = activeQuests.filter { it.category == category }
    fun questsByDifficulty(difficulty: QuestDifficulty): List<Quest> =
        activeQuests.filter { it.difficulty == difficulty }

    fun addListener(listener: QuestListener) {
        listeners.add(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.onQuestsChanged() }
    }

    companion object {
        fun getInstance(): QuestService =
            ApplicationManager.getApplication().getService(QuestService::class.java)
    }
}
