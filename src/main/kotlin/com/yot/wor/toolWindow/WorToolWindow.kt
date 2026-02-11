package com.yot.wor.toolWindow

import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.yot.wor.domain.ActionCategory
import com.yot.wor.domain.PlayerState
import com.yot.wor.export.exportToCsv
import com.yot.wor.export.exportToJson
import com.yot.wor.export.exportToMarkdown
import com.yot.wor.services.PlayerStateService
import java.awt.*
import java.awt.FlowLayout.LEFT
import javax.swing.*
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.roundToInt

class WorToolWindow(private val project: Project) {
    private val playerStateService = PlayerStateService.getInstance()
    private val mainPanel = JBPanel<Nothing>()

    // UI Components
    private val iconLabel = JBLabel()
    private val titleLabel = JBLabel()
    private val levelLabel = JBLabel()
    private val xpLabel = JBLabel()
    private val progressBar = JProgressBar(0, 100)
    private val totalActionsLabel = JBLabel()
    private val categoryStatsPanel = JBPanel<Nothing>()
    private val recentActionsPanel = JBPanel<Nothing>()

    init {
        setupUI()
        updateUI(playerStateService.playerState())

        playerStateService.addListener { state ->
            updateUI(state)
        }
    }

    private fun setupUI() {
        mainPanel.layout = BorderLayout()

        // Configure progress bar with visible text
        progressBar.apply {
            isStringPainted = true

            // Custom UI to ensure text is always visible with high contrast
            setUI(object : BasicProgressBarUI() {
                override fun getSelectionForeground(): Color {
                    // Text color on filled portion - white for good contrast
                    return JBColor.WHITE
                }

                override fun getSelectionBackground(): Color {
                    // Text color on empty portion - use theme-aware color
                    return JBColor.foreground()
                }
            })
        }

        // Panel principal avec scroll
        val contentPanel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)
        }

        // Boutons d'export en haut
        contentPanel.add(createExportPanel())
        contentPanel.add(createSeparator())

        // Header - Titre et niveau
        contentPanel.add(createHeaderPanel())
        contentPanel.add(createSeparator())

        // XP et progression
        contentPanel.add(createXPPanel())
        contentPanel.add(createSeparator())

        // Statistiques globales
        contentPanel.add(createStatsPanel())
        contentPanel.add(createSeparator())

        // Statistiques par catégorie
        contentPanel.add(createCategoryStatsSection())
        contentPanel.add(createSeparator())

        // Actions récentes
        contentPanel.add(createRecentActionsSection())

        val scrollPane = JBScrollPane(contentPanel)
        mainPanel.add(scrollPane, BorderLayout.CENTER)
    }

    private fun createExportPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = FlowLayout(LEFT)
            border = JBUI.Borders.empty(5)

            val exportButton = JButton("📤 Export Stats")
            exportButton.addActionListener {
                showExportDialog()
            }

            add(exportButton)
        }
    }

    private fun showExportDialog() {
        val options = arrayOf("JSON", "CSV", "Markdown", "Cancel")
        val choice = Messages.showDialog(
            project,
            "Choose export format:",
            "Export Statistics",
            options,
            0,
            Messages.getQuestionIcon()
        )

        when (choice) {
            0 -> exportStats("json")
            1 -> exportStats("csv")
            2 -> exportStats("md")
        }
    }

    private fun exportStats(extension: String) {
        val descriptor = FileSaverDescriptor(
            "Export Statistics",
            "Choose where to save your statistics",
            extension
        )

        val dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project)
        val fileWrapper = dialog.save("wor-stats.$extension")

        if (fileWrapper != null) {
            val file = fileWrapper.file

            val success = when (extension) {
                "json" -> exportToJson(file)
                "csv" -> exportToCsv(file)
                "md" -> exportToMarkdown(file)
                else -> false
            }

            if (success) {
                Messages.showInfoMessage(
                    project,
                    "Statistics exported successfully!\n${file.absolutePath}",
                    "Export Successful"
                )
            } else {
                Messages.showErrorDialog(
                    project,
                    "Error exporting statistics",
                    "Export Error"
                )
            }
        }
    }

    private fun createHeaderPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BorderLayout()
            border = JBUI.Borders.empty(5)

            titleLabel.font = Font(titleLabel.font.name, Font.BOLD, 18)
            levelLabel.font = Font(levelLabel.font.name, Font.BOLD, 14)

            val textPanel = JBPanel<Nothing>().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                alignmentX = Component.LEFT_ALIGNMENT
                border = JBUI.Borders.emptyLeft(10)

                add(titleLabel)
                add(levelLabel)
            }

            val contentPanel = JBPanel<Nothing>().apply {
                layout = FlowLayout(LEFT, 5, 0)
                add(iconLabel)
                add(textPanel)
            }

            add(contentPanel, BorderLayout.WEST)
        }
    }

    private fun createXPPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(5)

            add(xpLabel)
            add(progressBar)
        }
    }

    private fun createStatsPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = GridBagLayout()
            border = BorderFactory.createTitledBorder("📊 Statistics")

            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
                gridx = 0
                gridy = 0
            }

            add(totalActionsLabel, gbc)
        }
    }

    private fun createCategoryStatsSection(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BorderLayout()
            border = BorderFactory.createTitledBorder("🎯 By Category")

            categoryStatsPanel.layout = BoxLayout(categoryStatsPanel, BoxLayout.Y_AXIS)
            add(categoryStatsPanel, BorderLayout.CENTER)
        }
    }

    private fun createRecentActionsSection(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BorderLayout()
            border = BorderFactory.createTitledBorder("📜 Recent Actions")

            recentActionsPanel.layout = BoxLayout(recentActionsPanel, BoxLayout.Y_AXIS)
            add(recentActionsPanel, BorderLayout.CENTER)
        }
    }

    private fun createSeparator(): JPanel {
        return JBPanel<Nothing>().apply {
            preferredSize = JBUI.size(0, 10)
        }
    }

    private fun updateUI(state: PlayerState) {
        val icon = state.levelIconImage
        if (icon != null) {
            iconLabel.icon = icon
            iconLabel.text = null
            titleLabel.text = state.title
        } else {
            iconLabel.icon = null
            iconLabel.text = state.levelIconEmoji
            titleLabel.text = state.title
        }
        levelLabel.text = "Level ${state.level} - ${state.levelTier}"

        // XP
        val currentXP = state.currentLevelXP
        val neededXP = state.xpForNextLevel - PlayerState.calculateXPForLevel(state.level)
        xpLabel.text = "💎 XP: $currentXP / $neededXP (Total: ${state.totalXP})"

        // Progress bar
        val progress = (state.levelProgress * 100).roundToInt()
        progressBar.value = progress
        progressBar.string = "$progress%"

        // Set color based on progress - visible in both light and dark themes
        progressBar.foreground = when {
            progress >= 75 -> JBColor.GREEN
            progress >= 50 -> JBColor(0x4A9EFF, 0x4A9EFF) // Bright blue
            progress >= 25 -> JBColor.ORANGE
            else -> JBColor(0x9370DB, 0xB19CD9) // Purple - visible in both themes
        }

        // Global stats
        totalActionsLabel.text = "🎯 Total actions: ${state.actionsHistory.size}"

        // Category stats
        updateCategoryStats(state)

        // Recent actions
        updateRecentActions(state)
    }

    private fun updateCategoryStats(state: PlayerState) {
        categoryStatsPanel.removeAll()

        ActionCategory.entries.forEach { category ->
            val stats = state.statisticsByCategory[category]
            if (stats != null && stats.actionCount > 0) {
                val panel = JBPanel<Nothing>().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    border = JBUI.Borders.empty(5)

                    add(JBLabel("${category.icon} ${category.displayName}"))
                    add(JBLabel("  Actions: ${stats.actionCount} | XP: ${stats.totalXP}"))
                    if (stats.mostUsedAction != null) {
                        add(JBLabel("  Most used: ${stats.mostUsedAction.displayName}"))
                    }
                }
                categoryStatsPanel.add(panel)
            }
        }

        categoryStatsPanel.revalidate()
        categoryStatsPanel.repaint()
    }

    private fun updateRecentActions(state: PlayerState) {
        recentActionsPanel.removeAll()

        val recentActions = state.actionsHistory.takeLast(10).reversed()

        if (recentActions.isEmpty()) {
            recentActionsPanel.add(JBLabel("No actions yet. Start refactoring! 🚀"))
        } else {
            recentActions.forEach { action ->
                val text = buildString {
                    append("${action.type.gameplayTag} ")
                    append(action.type.displayName)
                    append(" (+${action.xpReward} XP)")
                    action.fileName?.let { append(" - $it") }
                }
                recentActionsPanel.add(JBLabel(text).apply {
                    border = JBUI.Borders.empty(2)
                })
            }
        }

        recentActionsPanel.revalidate()
        recentActionsPanel.repaint()
    }

    fun getContent(): JPanel = mainPanel
}
