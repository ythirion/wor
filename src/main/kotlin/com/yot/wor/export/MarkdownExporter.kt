package com.yot.wor.export

import com.intellij.openapi.project.Project
import com.yot.wor.domain.PlayerState
import com.yot.wor.domain.Quest
import com.yot.wor.icons.LevelIcons
import com.yot.wor.services.PlayerStateService
import com.yot.wor.services.QuestService
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun exportToMarkdown(project: Project, file: File): Boolean {
    return try {
        val playerState = PlayerStateService.getInstance(project).getPlayerState()
        val quests = QuestService.getInstance(project).getActiveQuests()
        val completedQuests = QuestService.getInstance(project).getCompletedQuests()

        val markdown = buildMarkdownString(playerState, quests, completedQuests)
        file.writeText(markdown)

        copyLevelIconToExportDir(file.parentFile, playerState.level)

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun buildMarkdownString(
    playerState: PlayerState,
    quests: List<Quest>,
    completedQuests: List<Quest>
): String = buildString {
    appendLine("# 🎮 World of Refactoring - Statistics")
    appendLine()
    appendLine("**Export date:** ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))}")
    appendLine()

    // Player profile with icon
    appendLine("## 👤 Profile")
    appendLine()

    // Try to include icon image if available
    val iconPath = LevelIcons.iconPathForLevel(playerState.level)
    val iconFileName = iconPath.substringAfterLast('/')
    if (playerState.levelIconImage != null) {
        appendLine("![Level Icon]($iconFileName)")
        appendLine()
    }

    appendLine("- **Title:** ${playerState.levelIconEmoji} ${playerState.title}")
    appendLine("- **Level:** ${playerState.level} - ${playerState.levelTier}")
    appendLine("- **Total XP:** ${playerState.totalXP}")
    appendLine("- **Actions performed:** ${playerState.actionsHistory.size}")
    appendLine()

    // Category stats
    appendLine("## 📊 Statistics by Category")
    appendLine()
    appendLine("| Category | Actions | Total XP | Avg XP | Most Used |")
    appendLine("|----------|---------|----------|--------|-----------|")

    playerState.statisticsByCategory.forEach { (category, stats) ->
        appendLine(
            "| ${category.icon} ${category.displayName} | ${stats.actionCount} | ${stats.totalXP} | %.2f | ${stats.mostUsedAction?.displayName ?: "N/A"} |".format(
                stats.averageXP
            )
        )
    }
    appendLine()

    // Quests
    appendLine("## 📜 Quests")
    appendLine()
    appendLine("- **Active:** ${quests.size}")
    appendLine("- **Completed:** ${completedQuests.size}")
    appendLine()

    // Top actions
    appendLine("## 🏆 Top 10 Actions")
    appendLine()
    val topActions = playerState.actionsHistory
        .groupingBy { it.type }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(10)

    appendLine("| Rank | Action | Count |")
    appendLine("|------|--------|-------|")
    topActions.forEachIndexed { index, (type, count) ->
        appendLine("| ${index + 1} | ${type.gameplayTag} ${type.displayName} | $count |")
    }
}

private fun copyLevelIconToExportDir(exportDir: File, level: Int) {
    try {
        val icon = LevelIcons.iconForLevel(level)
        if (icon != null) {
            val iconPath = LevelIcons.iconPathForLevel(level)
            val iconFileName = iconPath.substringAfterLast('/')

            // Try to read the icon from resources
            val resourceStream = LevelIcons::class.java.getResourceAsStream("/$iconPath")
            if (resourceStream != null) {
                val targetFile = File(exportDir, iconFileName)
                resourceStream.use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    } catch (_: Exception) {
    }
}
