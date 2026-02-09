package com.yot.wor.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.yot.wor.domain.Quest
import com.yot.wor.domain.QuestCategory
import com.yot.wor.domain.QuestStatus
import com.yot.wor.services.QuestService
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JProgressBar
import kotlin.math.roundToInt

class QuestsPanel(project: Project) {
    private val questService = QuestService.getInstance(project)
    private val mainPanel = JBPanel<Nothing>()
    private val questsListPanel = JBPanel<Nothing>()
    private val statsLabel = JBLabel()

    init {
        setupUI()
        updateUI()

        questService.addListener {
            updateUI()
        }
    }

    private fun setupUI() {
        mainPanel.layout = BorderLayout()

        val headerPanel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)

            statsLabel.font = Font(statsLabel.font.name, Font.BOLD, 14)
            add(statsLabel)
        }

        mainPanel.add(headerPanel, BorderLayout.NORTH)
        questsListPanel.layout = BoxLayout(questsListPanel, BoxLayout.Y_AXIS)

        val scrollPane = JBScrollPane(questsListPanel)
        mainPanel.add(scrollPane, BorderLayout.CENTER)
    }

    private fun updateUI() {
        val activeQuests = questService.activeQuests()
        val completedQuests = questService.completedQuests()

        statsLabel.text = "📜 ${activeQuests.size} active quests | ✅ ${completedQuests.size} completed"
        questsListPanel.removeAll()

        if (activeQuests.isEmpty()) {
            questsListPanel.add(JBLabel("✨ No quests available at the moment").apply {
                border = JBUI.Borders.empty(20)
            })
        } else {
            val questsByCategory = activeQuests.groupBy { it.category }

            QuestCategory.entries.forEach { category ->
                val categoryQuests = questsByCategory[category] ?: emptyList()
                if (categoryQuests.isNotEmpty()) {
                    questsListPanel.add(createCategorySection(category, categoryQuests))
                }
            }
        }

        questsListPanel.revalidate()
        questsListPanel.repaint()
    }

    private fun createCategorySection(category: QuestCategory, quests: List<Quest>): JPanel {
        val panel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                JBUI.Borders.empty(10, 5),
                BorderFactory.createTitledBorder("${category.icon} ${category.displayName}")
            )
        }

        quests.forEach { quest ->
            panel.add(createQuestCard(quest))
        }

        return panel
    }

    private fun createQuestCard(quest: Quest): JPanel {
        val card = JBPanel<Nothing>().apply {
            layout = BorderLayout()
            border = BorderFactory.createCompoundBorder(
                JBUI.Borders.empty(5),
                BorderFactory.createEtchedBorder()
            )
        }

        val contentPanel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)
        }

        val titleLabel = JBLabel(buildString {
            append(quest.difficulty.icon)
            append(" ")
            append(quest.title)
            append(" ")
            append("(${(quest.xpReward * quest.difficulty.xpMultiplier).toInt()} XP)")
        }).apply {
            font = Font(font.name, Font.BOLD, 13)
        }
        contentPanel.add(titleLabel)

        val descLabel = JBLabel("<html>${quest.description}</html>").apply {
            foreground = JBColor.GRAY
            border = JBUI.Borders.empty(5, 0)
        }
        contentPanel.add(descLabel)

        quest.objectives.forEach { objective ->
            val objectivePanel = JBPanel<Nothing>().apply {
                layout = BorderLayout()
                border = JBUI.Borders.empty(3, 10)
            }

            val icon = if (objective.isCompleted) "✅" else "⬜"
            val objectiveLabel =
                JBLabel("$icon ${objective.description}: ${objective.currentCount}/${objective.targetCount}")

            objectivePanel.add(objectiveLabel, BorderLayout.WEST)

            if (!objective.isCompleted) {
                val progressBar = JProgressBar(0, 100).apply {
                    value = (objective.progress * 100).roundToInt()
                    isStringPainted = true
                    string = "${(objective.progress * 100).roundToInt()}%"
                    preferredSize = java.awt.Dimension(100, 20)
                }
                objectivePanel.add(progressBar, BorderLayout.EAST)
            }

            contentPanel.add(objectivePanel)
        }

        val globalProgress = JProgressBar(0, 100).apply {
            value = (quest.progress * 100).roundToInt()
            isStringPainted = true
            string = "Progress: ${(quest.progress * 100).roundToInt()}%"
            border = JBUI.Borders.emptyTop(5)

            foreground = when {
                quest.progress >= 1.0 -> JBColor.GREEN
                quest.progress >= 0.5 -> JBColor.ORANGE
                else -> JBColor.LIGHT_GRAY
            }
        }
        contentPanel.add(globalProgress)

        if (quest.status == QuestStatus.IN_PROGRESS) {
            val statusLabel = JBLabel("🔥 In progress...").apply {
                foreground = JBColor.ORANGE
                font = Font(font.name, Font.ITALIC, 11)
                border = JBUI.Borders.emptyTop(5)
            }
            contentPanel.add(statusLabel)
        }

        card.add(contentPanel, BorderLayout.CENTER)

        return card
    }

    fun getContent(): JPanel = mainPanel
}
