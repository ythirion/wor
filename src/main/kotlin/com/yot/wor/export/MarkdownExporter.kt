package com.yot.wor.export

import com.yot.wor.domain.PlayerState
import com.yot.wor.domain.Quest
import com.yot.wor.icons.LevelIcons
import com.yot.wor.services.PlayerStateService
import com.yot.wor.services.QuestService
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun exportToMarkdown(file: File): Boolean {
    return try {
        val playerState = PlayerStateService.getInstance().playerState()
        val quests = QuestService.getInstance().activeQuests()
        val completedQuests = QuestService.getInstance().completedQuests()

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

    // Completed Quests Details
    if (completedQuests.isNotEmpty()) {
        appendLine("### ✅ Completed Quests")
        appendLine()
        appendLine("| Quest | Description | XP Earned | Completed At |")
        appendLine("|-------|-------------|-----------|--------------|")

        completedQuests.forEach { quest ->
            val questXP = (quest.xpReward * quest.difficulty.xpMultiplier).toInt()
            val completedDate = quest.completedAt?.let {
                LocalDateTime.ofInstant(it, java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
            } ?: "N/A"

            appendLine("| ${quest.difficulty.icon} ${quest.title} | ${quest.description} | $questXP XP | $completedDate |")
        }
        appendLine()
    }

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
    val icon = LevelIcons.iconForLevel(level)
    if (icon != null) {
        val iconPath = LevelIcons.iconPathForLevel(level)
        val iconFileName = iconPath.substringAfterLast('/')

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
}
