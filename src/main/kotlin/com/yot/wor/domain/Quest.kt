package com.yot.wor.domain

import java.time.Instant

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
    val isCompleted: Boolean = objectives.all { it.isCompleted }
    val progress: Double
        get() {
            if (objectives.isEmpty()) return 0.0
            val completedCount = objectives.count { it.isCompleted }
            return completedCount.toDouble() / objectives.size
        }
}

data class QuestObjective(
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0
) {
    val isCompleted: Boolean = currentCount >= targetCount
    val progress: Double = if (targetCount > 0) currentCount.toDouble() / targetCount else 0.0
}

enum class QuestCategory(val displayName: String, val icon: String) {
    REFACTORING("Refactoring", "♻️"),
    TESTING("Testing", "🧪"),
    CLEANUP("Cleanup", "🧹"),
    DESIGN("Architecture", "🏗️"),
    DAILY("Daily", "📅")
}

enum class QuestDifficulty(val displayName: String, val icon: String, val xpMultiplier: Double) {
    EASY("Easy", "⭐", 1.0),
    MEDIUM("Medium", "⭐⭐", 1.5),
    HARD("Hard", "⭐⭐⭐", 2.0),
    EXPERT("Expert", "⭐⭐⭐⭐", 3.0)
}

enum class QuestStatus {
    AVAILABLE,      // Available
    IN_PROGRESS,    // In progress
    COMPLETED       // Completed
}
