package com.yot.wor.domain

import com.yot.wor.icons.LevelIcons
import java.time.Instant
import javax.swing.Icon
import kotlin.math.pow

data class PlayerState(
    val totalXP: Int = 0,
    val level: Int = 1,
    val actionsHistory: List<RefactoringAction> = emptyList(),
    val statisticsByCategory: Map<ActionCategory, CategoryStats> = emptyMap(),
    val lastActionTimestamp: Instant? = null
) {
    val xpForNextLevel: Int = calculateXPForLevel(level + 1)
    val currentLevelXP: Int = totalXP - calculateXPForLevel(level)
    val levelProgress: Double
        get() {
            val xpNeededForNextLevel = xpForNextLevel - calculateXPForLevel(level)
            return if (xpNeededForNextLevel > 0) {
                currentLevelXP.toDouble() / xpNeededForNextLevel
            } else 0.0
        }

    val title: String = when {
        level < 5 -> "Refactoring Apprentice"
        level < 10 -> "Refactorer"
        level < 20 -> "Expert Refactorer"
        level < 30 -> "Refactoring Master"
        level <= 50 -> "Grand Master"
        else -> "Living Legend"
    }

    val levelIconImage: Icon? = LevelIcons.iconForLevel(level)
    val levelIconEmoji: String = LevelIcons.emojiFallback(level)
    val levelIcon: String = levelIconEmoji
    val levelTier: String = LevelIcons.levelTierName(level)

    companion object {
        fun calculateXPForLevel(level: Int) = if (level <= 1) 0 else (100 * level.toDouble().pow(1.5)).toInt()

        fun calculateLevel(totalXP: Int): Int {
            var level = 1
            while (calculateXPForLevel(level + 1) <= totalXP) level++
            return level
        }
    }
}

data class CategoryStats(
    val category: ActionCategory,
    val actionCount: Int = 0,
    val totalXP: Int = 0,
    val mostUsedAction: RefactoringActionType? = null
) {
    val averageXP: Double = if (actionCount > 0) totalXP.toDouble() / actionCount else 0.0
}
