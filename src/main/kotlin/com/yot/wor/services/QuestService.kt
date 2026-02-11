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
        // Generate starter quests if this is the first time (no persisted state)
        // If state exists, loadState() will be called and will override this
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
                description = "Clean your code with 20 cleanup actions",
                category = QuestCategory.CLEANUP,
                xpReward = 250,
                difficulty = QuestDifficulty.MEDIUM,
                objectives = listOf(
                    QuestObjective("Optimize Imports", targetCount = 10),
                    QuestObjective("Reformat Code", targetCount = 10)
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
                    QuestObjective("Extract Class × 3", targetCount = 3),
                    QuestObjective("Move Method × 5", targetCount = 5)
                )
            )
        )
    }

    fun updateQuestProgress(action: RefactoringAction) {
        var questsUpdated = false

        activeQuests
            .filter { quest -> quest.isAvailable }
            .forEach { quest ->
                val updatedObjectives = quest.objectives.map { objective ->
                    if (shouldUpdateObjective(
                            objective,
                            action
                        )
                    ) objective.copy(currentCount = objective.currentCount + 1) else objective
                }

                val updatedQuest = quest.copy(
                    objectives = updatedObjectives,
                    status = if (updatedObjectives.all { it.isCompleted }) QuestStatus.IN_PROGRESS else quest.status
                )

                if (updatedQuest.isCompleted) {
                    completeQuest(updatedQuest)
                    questsUpdated = true
                } else if (updatedQuest != quest) {
                    val index = activeQuests.indexOf(quest)

                    if (index >= 0) {
                        activeQuests[index] = updatedQuest
                        questsUpdated = true
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
                "Extract Class",
                ignoreCase = true
            ) && action.type == RefactoringActionType.EXTRACT_CLASS -> true

            objective.description.contains(
                "Move Method",
                ignoreCase = true
            ) && action.type == RefactoringActionType.MOVE_METHOD -> true

            objective.description.contains(
                "Optimize Imports",
                ignoreCase = true
            ) && action.type == RefactoringActionType.OPTIMIZE_IMPORTS -> true

            objective.description.contains(
                "Reformat",
                ignoreCase = true
            ) && action.type == RefactoringActionType.REFORMAT_CODE -> true

            else -> false
        }
    }

    private fun completeQuest(quest: Quest) {
        val completedQuest = quest.copy(
            status = QuestStatus.COMPLETED,
            completedAt = Instant.now()
        )

        activeQuests.remove(quest)
        completedQuests.add(completedQuest)

        val questXP = (quest.xpReward * quest.difficulty.xpMultiplier).toInt()
        thisLogger().info("Quest completed: ${quest.title} (+$questXP XP)")
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
