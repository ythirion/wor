package com.yot.craftrpg.export

import com.intellij.openapi.project.Project
import com.yot.craftrpg.domain.PlayerState
import com.yot.craftrpg.icons.LevelIcons
import com.yot.craftrpg.services.PlayerStateService
import com.yot.craftrpg.services.QuestService
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Exporte les statistiques du joueur
 */
class StatsExporter(private val project: Project) {

    /**
     * Exporte les stats en JSON
     */
    fun exportToJson(file: File): Boolean {
        return try {
            val playerState = PlayerStateService.getInstance(project).getPlayerState()
            val quests = QuestService.getInstance(project).getActiveQuests()
            val completedQuests = QuestService.getInstance(project).getCompletedQuests()

            val json = buildJsonString(playerState, quests, completedQuests)
            file.writeText(json)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Exporte les stats en CSV
     */
    fun exportToCsv(file: File): Boolean {
        return try {
            val playerState = PlayerStateService.getInstance(project).getPlayerState()

            val csv = buildString {
                // Header
                appendLine("Metric,Value")

                // Stats générales
                appendLine("Level,${playerState.level}")
                appendLine("Total XP,${playerState.totalXP}")
                appendLine("Title,${playerState.title}")
                appendLine("Total Actions,${playerState.actionsHistory.size}")

                // Stats par catégorie
                playerState.statisticsByCategory.forEach { (category, stats) ->
                    appendLine("${category.displayName} - Actions,${stats.actionCount}")
                    appendLine("${category.displayName} - XP,${stats.totalXP}")
                    appendLine("${category.displayName} - Avg XP,${stats.averageXP}")
                    stats.mostUsedAction?.let {
                        appendLine("${category.displayName} - Most Used,${it.displayName}")
                    }
                }

                // Actions récentes
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

    /**
     * Exporte les stats en Markdown
     */
    fun exportToMarkdown(file: File): Boolean {
        return try {
            val playerState = PlayerStateService.getInstance(project).getPlayerState()
            val quests = QuestService.getInstance(project).getActiveQuests()
            val completedQuests = QuestService.getInstance(project).getCompletedQuests()

            val markdown = buildMarkdownString(playerState, quests, completedQuests)
            file.writeText(markdown)

            // Copy level icon to export directory if available
            copyLevelIconToExportDir(file.parentFile, playerState.level)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Copie l'icône de niveau dans le dossier d'export
     */
    private fun copyLevelIconToExportDir(exportDir: File, level: Int) {
        try {
            val icon = LevelIcons.getIconForLevel(level)
            if (icon != null) {
                val iconPath = LevelIcons.getIconPathForLevel(level)
                val iconFileName = iconPath.substringAfterLast('/')

                // Try to read the icon from resources
                val resourceStream = javaClass.getResourceAsStream("/$iconPath")
                if (resourceStream != null) {
                    val targetFile = File(exportDir, iconFileName)
                    resourceStream.use { input ->
                        targetFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Silently fail if icon can't be copied
        }
    }

    private fun buildJsonString(
        playerState: PlayerState,
        quests: List<com.yot.craftrpg.domain.Quest>,
        completedQuests: List<com.yot.craftrpg.domain.Quest>
    ): String {
        return buildString {
            appendLine("{")
            appendLine("""  "exportDate": "${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}",""")
            appendLine("""  "player": {""")
            appendLine("""    "level": ${playerState.level},""")
            appendLine("""    "totalXP": ${playerState.totalXP},""")
            appendLine("""    "title": "${playerState.title}",""")
            appendLine("""    "totalActions": ${playerState.actionsHistory.size}""")
            appendLine("""  },""")

            // Stats par catégorie
            appendLine("""  "categoryStats": [""")
            val categoryEntries = playerState.statisticsByCategory.entries.toList()
            categoryEntries.forEachIndexed { index, (category, stats) ->
                appendLine("""    {""")
                appendLine("""      "category": "${category.displayName}",""")
                appendLine("""      "actionCount": ${stats.actionCount},""")
                appendLine("""      "totalXP": ${stats.totalXP},""")
                appendLine("""      "averageXP": ${stats.averageXP},""")
                appendLine("""      "mostUsedAction": "${stats.mostUsedAction?.displayName ?: "N/A"}" """)
                append("""    }""")
                if (index < categoryEntries.size - 1) appendLine(",")
                else appendLine()
            }
            appendLine("""  ],""")

            // Quêtes
            appendLine("""  "activeQuests": ${quests.size},""")
            appendLine("""  "completedQuests": ${completedQuests.size}""")
            appendLine("}")
        }
    }

    private fun buildMarkdownString(
        playerState: PlayerState,
        quests: List<com.yot.craftrpg.domain.Quest>,
        completedQuests: List<com.yot.craftrpg.domain.Quest>
    ): String {
        return buildString {
            appendLine("# 🎮 Craft RPG - Statistics")
            appendLine()
            appendLine("**Export date:** ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))}")
            appendLine()

            // Player profile with icon
            appendLine("## 👤 Profile")
            appendLine()

            // Try to include icon image if available
            val iconPath = LevelIcons.getIconPathForLevel(playerState.level)
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
                appendLine("| ${category.icon} ${category.displayName} | ${stats.actionCount} | ${stats.totalXP} | %.2f | ${stats.mostUsedAction?.displayName ?: "N/A"} |".format(stats.averageXP))
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
    }
}
