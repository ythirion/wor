package com.yot.wor.toolWindow

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.yot.wor.domain.ActionCategory
import com.yot.wor.domain.RefactoringActionType
import java.awt.Component
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel

class RefactoringsPanel {
    private val mainPanel = JBPanel<Nothing>()

    init {
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)

        val contentPanel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)
        }

        ActionCategory.entries.forEach { category ->
            val types = RefactoringActionType.entries.filter { it.category == category }
            if (types.isNotEmpty()) {
                contentPanel.add(createCategorySection(category, types))
            }
        }

        val scrollPane = JBScrollPane(contentPanel)
        mainPanel.add(scrollPane)
    }

    private fun createCategorySection(category: ActionCategory, types: List<RefactoringActionType>): JPanel {
        val panel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                JBUI.Borders.empty(8, 4),
                BorderFactory.createTitledBorder("${category.icon} ${category.displayName}")
            )
            alignmentX = Component.LEFT_ALIGNMENT
        }

        types.forEach { type ->
            panel.add(createRefactoringCard(type))
        }

        return panel
    }

    private fun createRefactoringCard(type: RefactoringActionType): JPanel {
        val card = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                JBUI.Borders.empty(4, 2),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(JBColor(0xDDDDDD, 0x444444), 1, true),
                    JBUI.Borders.empty(8, 10),
                )
            )
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val headerPanel = JBPanel<Nothing>().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val nameLabel = JBLabel(type.displayName).apply {
            font = Font(font.name, Font.BOLD, 13)
        }

        val xpLabel = JBLabel("  +${type.baseXP} XP").apply {
            font = Font(font.name, Font.BOLD, 12)
            foreground = JBColor(0x2E7D32, 0x81C784)
        }

        val tagLabel = JBLabel("   ${type.gameplayTag}").apply {
            font = Font(font.name, Font.ITALIC, 11)
            foreground = JBColor.GRAY
        }

        headerPanel.add(nameLabel)
        headerPanel.add(xpLabel)
        headerPanel.add(tagLabel)
        card.add(headerPanel)

        val descLabel = JBLabel("<html><body style='width:420px'>${type.description}</body></html>").apply {
            border = JBUI.Borders.emptyTop(4)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        card.add(descLabel)

        val howToLabel = JBLabel("<html><body style='width:420px'><i>💡 ${type.howTo}</i></body></html>").apply {
            border = JBUI.Borders.emptyTop(4)
            foreground = JBColor(0x5C6BC0, 0x9FA8DA)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        card.add(howToLabel)

        return card
    }

    fun getContent(): JPanel = mainPanel
}
