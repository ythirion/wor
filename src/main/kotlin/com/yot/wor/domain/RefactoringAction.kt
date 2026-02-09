package com.yot.wor.domain

import java.time.Instant

data class RefactoringAction(
    val type: RefactoringActionType,
    val timestamp: Instant = Instant.now(),
    val fileName: String? = null,
    val elementName: String? = null
) {
    val xpReward: Int = type.baseXP
    val category: ActionCategory = type.category
}
