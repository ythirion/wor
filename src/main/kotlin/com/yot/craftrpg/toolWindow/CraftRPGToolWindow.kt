package com.yot.craftrpg.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.yot.craftrpg.domain.ActionCategory
import com.yot.craftrpg.domain.PlayerState
import com.yot.craftrpg.services.PlayerStateService
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JProgressBar
import kotlin.math.roundToInt

/**
 * Panneau principal de la fenêtre d'outils Craft RPG
 */
class CraftRPGToolWindow(private val project: Project) {

    private val playerStateService = PlayerStateService.getInstance(project)
    private val mainPanel = JBPanel<Nothing>()

    // UI Components
    private val titleLabel = JBLabel()
    private val levelLabel = JBLabel()
    private val xpLabel = JBLabel()
    private val progressBar = JProgressBar(0, 100)
    private val totalActionsLabel = JBLabel()
    private val categoryStatsPanel = JBPanel<Nothing>()
    private val recentActionsPanel = JBPanel<Nothing>()

    init {
        setupUI()
        updateUI(playerStateService.getPlayerState())

        // S'abonner aux changements d'état
        playerStateService.addListener { state ->
            updateUI(state)
        }
    }

    private fun setupUI() {
        mainPanel.layout = BorderLayout()

        // Panel principal avec scroll
        val contentPanel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)
        }

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

    private fun createHeaderPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(5)

            titleLabel.font = Font(titleLabel.font.name, Font.BOLD, 18)
            levelLabel.font = Font(levelLabel.font.name, Font.BOLD, 14)

            add(titleLabel)
            add(levelLabel)
        }
    }

    private fun createXPPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(5)

            add(xpLabel)
            add(progressBar.apply {
                isStringPainted = true
            })
        }
    }

    private fun createStatsPanel(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = GridBagLayout()
            border = BorderFactory.createTitledBorder("📊 Statistiques")

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
            border = BorderFactory.createTitledBorder("🎯 Par Catégorie")

            categoryStatsPanel.layout = BoxLayout(categoryStatsPanel, BoxLayout.Y_AXIS)
            add(categoryStatsPanel, BorderLayout.CENTER)
        }
    }

    private fun createRecentActionsSection(): JPanel {
        return JBPanel<Nothing>().apply {
            layout = BorderLayout()
            border = BorderFactory.createTitledBorder("📜 Actions Récentes")

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
        // Header
        titleLabel.text = state.title
        levelLabel.text = "Niveau ${state.level}"

        // XP
        val currentXP = state.currentLevelXP
        val neededXP = state.xpForNextLevel - PlayerState.calculateXPForLevel(state.level)
        xpLabel.text = "💎 XP: $currentXP / $neededXP (Total: ${state.totalXP})"

        // Progress bar
        val progress = (state.levelProgress * 100).roundToInt()
        progressBar.value = progress
        progressBar.string = "$progress%"

        // Stats globales
        totalActionsLabel.text = "🎯 Actions totales: ${state.actionsHistory.size}"

        // Stats par catégorie
        updateCategoryStats(state)

        // Actions récentes
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
                        add(JBLabel("  Plus utilisé: ${stats.mostUsedAction.displayName}"))
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
            recentActionsPanel.add(JBLabel("Aucune action encore. Commencez à refactorer ! 🚀"))
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
