package com.yot.wor.export

import com.intellij.openapi.project.Project
import com.yot.wor.domain.PlayerState
import com.yot.wor.domain.Quest
import com.yot.wor.services.PlayerStateService
import com.yot.wor.services.QuestService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class StatsExport(
    val exportDate: String,
    val player: PlayerInfo,
    val categoryStats: List<CategoryInfo>,
    val activeQuests: Int,
    val completedQuests: Int
)

@Serializable
data class PlayerInfo(
    val level: Int,
    val totalXP: Int,
    val title: String,
    val totalActions: Int
)

@Serializable
data class CategoryInfo(
    val category: String,
    val actionCount: Int,
    val totalXP: Int,
    val averageXP: Double,
    val mostUsedAction: String
)

private val json = Json { prettyPrint = true }

fun exportToJson(project: Project, file: File): Boolean {
    return try {
        val playerState = PlayerStateService.getInstance(project).getPlayerState()
        val quests = QuestService.getInstance(project).getActiveQuests()
        val completedQuests = QuestService.getInstance(project).getCompletedQuests()

        val export = buildStatsExport(playerState, quests, completedQuests)
        val jsonString = json.encodeToString(export)

        file.writeText(jsonString)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun buildStatsExport(
    playerState: PlayerState,
    quests: List<Quest>,
    completedQuests: List<Quest>
): StatsExport =
    StatsExport(
        exportDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        player = PlayerInfo(
            level = playerState.level,
            totalXP = playerState.totalXP,
            title = playerState.title,
            totalActions = playerState.actionsHistory.size
        ),
        categoryStats = playerState.statisticsByCategory.map { (category, stats) ->
            CategoryInfo(
                category = category.displayName,
                actionCount = stats.actionCount,
                totalXP = stats.totalXP,
                averageXP = stats.averageXP,
                mostUsedAction = stats.mostUsedAction?.displayName ?: "N/A"
            )
        },
        activeQuests = quests.size,
        completedQuests = completedQuests.size
    )