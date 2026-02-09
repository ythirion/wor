package com.yot.craftrpg.domain

import java.time.Instant

/**
 * Représente une quête pour le joueur
 */
data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val category: QuestCategory,
    val xpReward: Int,
    val difficulty: QuestDifficulty,
    val objectives: List<QuestObjective>,
    val status: QuestStatus = QuestStatus.AVAILABLE,
    val createdAt: Instant = Instant.now(),
    val completedAt: Instant? = null
) {
    /**
     * Vérifie si la quête est complétée
     */
    val isCompleted: Boolean
        get() = objectives.all { it.isCompleted }

    /**
     * Progression de la quête (0.0 à 1.0)
     */
    val progress: Double
        get() {
            if (objectives.isEmpty()) return 0.0
            val completedCount = objectives.count { it.isCompleted }
            return completedCount.toDouble() / objectives.size
        }
}

/**
 * Objectif d'une quête
 */
data class QuestObjective(
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0
) {
    val isCompleted: Boolean
        get() = currentCount >= targetCount

    val progress: Double
        get() = if (targetCount > 0) currentCount.toDouble() / targetCount else 0.0
}

/**
 * Quest category
 */
enum class QuestCategory(val displayName: String, val icon: String) {
    REFACTORING("Refactoring", "♻️"),
    TESTING("Testing", "🧪"),
    CLEANUP("Cleanup", "🧹"),
    DESIGN("Architecture", "🏗️"),
    DAILY("Daily", "📅")
}

/**
 * Quest difficulty
 */
enum class QuestDifficulty(val displayName: String, val icon: String, val xpMultiplier: Double) {
    EASY("Easy", "⭐", 1.0),
    MEDIUM("Medium", "⭐⭐", 1.5),
    HARD("Hard", "⭐⭐⭐", 2.0),
    EXPERT("Expert", "⭐⭐⭐⭐", 3.0)
}

/**
 * Quest status
 */
enum class QuestStatus {
    AVAILABLE,      // Available
    IN_PROGRESS,    // In progress
    COMPLETED,      // Completed
    FAILED,         // Failed
    EXPIRED         // Expired
}
