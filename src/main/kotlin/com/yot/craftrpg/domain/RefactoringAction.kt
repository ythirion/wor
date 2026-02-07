package com.yot.craftrpg.domain

import java.time.Instant

/**
 * Représente une action de refactoring détectée
 */
data class RefactoringAction(
    val type: RefactoringActionType,
    val timestamp: Instant = Instant.now(),
    val fileName: String? = null,
    val elementName: String? = null
) {
    val xpReward: Int
        get() = type.baseXP

    val category: ActionCategory
        get() = type.category
}
