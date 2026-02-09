package com.yot.craftrpg.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.yot.craftrpg.domain.*
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Service pour gérer les quêtes
 */
@Service(Service.Level.PROJECT)
class QuestService(private val project: Project) {

    private val activeQuests = CopyOnWriteArrayList<Quest>()
    private val completedQuests = CopyOnWriteArrayList<Quest>()
    private val listeners = CopyOnWriteArrayList<QuestListener>()

    /**
     * Interface pour écouter les changements de quêtes
     */
    fun interface QuestListener {
        fun onQuestsChanged()
    }

    init {
        // Générer des quêtes de démarrage
        generateStarterQuests()

        // S'abonner aux actions de refactoring pour mettre à jour les quêtes
        RefactoringDetectionService.getInstance(project).addListener { action ->
            updateQuestProgress(action)
        }
    }

    /**
     * Generates starter quests
     */
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

    /**
     * Met à jour la progression des quêtes en fonction d'une action
     */
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

    /**
     * Vérifie si un objectif doit être mis à jour pour une action donnée
     */
    private fun shouldUpdateObjective(objective: QuestObjective, action: RefactoringAction): Boolean {
        return when {
            objective.description.contains("refactoring", ignoreCase = true) -> true
            objective.description.contains("Renommer", ignoreCase = true) && action.type == RefactoringActionType.RENAME -> true
            objective.description.contains("Extract Method", ignoreCase = true) && action.type == RefactoringActionType.EXTRACT_METHOD -> true
            objective.description.contains("Extract Class", ignoreCase = true) && action.type == RefactoringActionType.EXTRACT_CLASS -> true
            objective.description.contains("Move Method", ignoreCase = true) && action.type == RefactoringActionType.MOVE_METHOD -> true
            objective.description.contains("Optimize Imports", ignoreCase = true) && action.type == RefactoringActionType.OPTIMIZE_IMPORTS -> true
            objective.description.contains("Reformat", ignoreCase = true) && action.type == RefactoringActionType.REFORMAT_CODE -> true
            else -> false
        }
    }

    /**
     * Complète une quête et donne l'XP au joueur
     */
    private fun completeQuest(quest: Quest) {
        val completedQuest = quest.copy(
            status = QuestStatus.COMPLETED,
            completedAt = Instant.now()
        )

        activeQuests.remove(quest)
        completedQuests.add(completedQuest)

        // Donner l'XP au joueur
        val playerStateService = PlayerStateService.getInstance(project)
        val questXP = (quest.xpReward * quest.difficulty.xpMultiplier).toInt()

        // Créer une action fictive pour l'XP de la quête
        val questAction = RefactoringAction(
            type = RefactoringActionType.RENAME, // Type fictif
            fileName = "Quest Completed"
        )

        // TODO: Créer un système d'XP bonus séparé pour les quêtes
        thisLogger().info("Quest completed: ${quest.title} (+$questXP XP)")
    }

    /**
     * Ajoute une nouvelle quête
     */
    fun addQuest(quest: Quest) {
        activeQuests.add(quest)
        notifyListeners()
    }

    /**
     * Récupère toutes les quêtes actives
     */
    fun getActiveQuests(): List<Quest> = activeQuests.toList()

    /**
     * Récupère les quêtes complétées
     */
    fun getCompletedQuests(): List<Quest> = completedQuests.toList()

    /**
     * Récupère les quêtes par catégorie
     */
    fun getQuestsByCategory(category: QuestCategory): List<Quest> {
        return activeQuests.filter { it.category == category }
    }

    /**
     * Récupère les quêtes par difficulté
     */
    fun getQuestsByDifficulty(difficulty: QuestDifficulty): List<Quest> {
        return activeQuests.filter { it.difficulty == difficulty }
    }

    fun addListener(listener: QuestListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: QuestListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.onQuestsChanged() }
    }

    companion object {
        fun getInstance(project: Project): QuestService =
            project.getService(QuestService::class.java)
    }
}
