package com.yot.wor.export

import com.intellij.openapi.project.Project
import com.yot.wor.services.PlayerStateService
import java.io.File

fun exportToCsv(project: Project, file: File): Boolean {
    return try {
        val playerState = PlayerStateService.getInstance(project).playerState()

        val csv = buildString {
            // Header
            appendLine("Metric,Value")

            // General stats
            appendLine("Level,${playerState.level}")
            appendLine("Total XP,${playerState.totalXP}")
            appendLine("Title,${playerState.title}")
            appendLine("Total Actions,${playerState.actionsHistory.size}")

            // Stats by category
            playerState.statisticsByCategory.forEach { (category, stats) ->
                appendLine("${category.displayName} - Actions,${stats.actionCount}")
                appendLine("${category.displayName} - XP,${stats.totalXP}")
                appendLine("${category.displayName} - Avg XP,${stats.averageXP}")
                stats.mostUsedAction?.let {
                    appendLine("${category.displayName} - Most Used,${it.displayName}")
                }
            }

            // Recent actions
            appendLine()
            appendLine("Recent Actions")
            appendLine("Timestamp,Action,XP,File")
            playerState.actionsHistory.takeLast(50).forEach { action ->
                appendLine("${action.timestamp},${action.type.displayName},${action.xpReward},${action.fileName ?: ""}")
            }
        }

        file.writeText(csv)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}