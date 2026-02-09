package com.yot.craftrpg.domain

import java.time.Instant

/**
 * État du joueur avec XP, niveau, statistiques
 */
data class PlayerState(
    val totalXP: Int = 0,
    val level: Int = 1,
    val actionsHistory: List<RefactoringAction> = emptyList(),
    val statisticsByCategory: Map<ActionCategory, CategoryStats> = emptyMap(),
    val lastActionTimestamp: Instant? = null
) {
    /**
     * XP nécessaire pour le prochain niveau
     */
    val xpForNextLevel: Int
        get() = calculateXPForLevel(level + 1)

    /**
     * XP actuel dans le niveau courant
     */
    val currentLevelXP: Int
        get() = totalXP - calculateXPForLevel(level)

    /**
     * Progression vers le prochain niveau (0.0 à 1.0)
     */
    val levelProgress: Double
        get() {
            val xpInCurrentLevel = currentLevelXP
            val xpNeededForNextLevel = xpForNextLevel - calculateXPForLevel(level)
            return if (xpNeededForNextLevel > 0) {
                xpInCurrentLevel.toDouble() / xpNeededForNextLevel
            } else {
                0.0
            }
        }

    /**
     * Titre du joueur basé sur son niveau
     */
    val title: String
        get() = when {
            level < 5 -> "🌱 Apprenti Refactorer"
            level < 10 -> "⚔️ Refactorer"
            level < 20 -> "🛡️ Refactorer Expert"
            level < 30 -> "🎖️ Maître Refactorer"
            level < 50 -> "👑 Grand Maître"
            else -> "🧙 Légende Vivante"
        }

    companion object {
        /**
         * Calcule l'XP total nécessaire pour atteindre un niveau
         * Formule: XP = 100 * level^1.5
         */
        fun calculateXPForLevel(level: Int): Int {
            if (level <= 1) return 0
            return (100 * Math.pow(level.toDouble(), 1.5)).toInt()
        }

        /**
         * Calcule le niveau basé sur l'XP total
         */
        fun calculateLevel(totalXP: Int): Int {
            var level = 1
            while (calculateXPForLevel(level + 1) <= totalXP) {
                level++
            }
            return level
        }
    }
}

/**
 * Statistiques par catégorie d'actions
 */
data class CategoryStats(
    val category: ActionCategory,
    val actionCount: Int = 0,
    val totalXP: Int = 0,
    val mostUsedAction: RefactoringActionType? = null
) {
    val averageXP: Double = if (actionCount > 0) totalXP.toDouble() / actionCount else 0.0
}
