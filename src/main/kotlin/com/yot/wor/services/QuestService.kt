package com.yot.wor.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.yot.wor.domain.*
import java.time.Instant
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Service(Service.Level.PROJECT)
class QuestService(private val project: Project) {

    private val activeQuests = CopyOnWriteArrayList<Quest>()
    private val completedQuests = CopyOnWriteArrayList<Quest>()
    private val listeners = CopyOnWriteArrayList<QuestListener>()

    fun interface QuestListener {
        fun onQuestsChanged()
    }

    init {
        generateStarterQuests()
        RefactoringDetectionService
            .getInstance(project)
            .addListener { action -> updateQuestProgress(action) }
    }

    private fun generateStarterQuests() {
        // Daily quest: Do 5 refactorings
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

    private fun updateQuestProgress(action: RefactoringAction) {
        var questsUpdated = false

        for (quest in activeQuests) {
            if (quest.status != QuestStatus.AVAILABLE && quest.status != QuestStatus.IN_PROGRESS) {
                continue
            }

            val updatedObjectives = quest.objectives.map { objective ->
                if (shouldUpdateObjective(objective, action)) {
                    objective.copy(currentCount = objective.currentCount + 1)
                } else {
                    objective
                }
            }

            val updatedQuest = quest.copy(
                objectives = updatedObjectives,
                status = if (updatedObjectives.all { it.isCompleted }) QuestStatus.IN_PROGRESS else quest.status
            )

            if (updatedQuest.isCompleted && quest.status != QuestStatus.COMPLETED) {
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

        if (questsUpdated) {
            notifyListeners()
        }
    }

    private fun shouldUpdateObjective(objective: QuestObjective, action: RefactoringAction): Boolean {
        return when {
            objective.description.contains("refactoring", ignoreCase = true) -> true
            objective.description.contains(
                "Renommer",
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

        // Créer une action fictive pour l'XP de la quête
        val questAction = RefactoringAction(
            type = RefactoringActionType.RENAME, // Type fictif
            fileName = "Quest Completed"
        )

        // TODO: Créer un système d'XP bonus séparé pour les quêtes
        thisLogger().info("Quest completed: ${quest.title} (+$questXP XP)")
    }

    fun addQuest(quest: Quest) {
        activeQuests.add(quest)
        notifyListeners()
    }

    fun getActiveQuests(): List<Quest> = activeQuests.toList()
    fun getCompletedQuests(): List<Quest> = completedQuests.toList()
    fun getQuestsByCategory(category: QuestCategory): List<Quest> = activeQuests.filter { it.category == category }
    fun getQuestsByDifficulty(difficulty: QuestDifficulty): List<Quest> =
        activeQuests.filter { it.difficulty == difficulty }

    fun addListener(listener: QuestListener) {
        listeners.add(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.onQuestsChanged() }
    }

    companion object {
        fun getInstance(project: Project): QuestService =
            project.getService(QuestService::class.java)
    }
}
