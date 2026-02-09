package com.yot.craftrpg.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory pour créer la fenêtre d'outils Craft RPG
 */
class CraftRPGToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        // Onglet Stats
        val statsPanel = CraftRPGToolWindow(project)
        val statsContent = contentFactory.createContent(
            statsPanel.getContent(),
            "📊 Stats",
            false
        )
        toolWindow.contentManager.addContent(statsContent)

        // Onglet Quêtes
        val questsPanel = QuestsPanel(project)
        val questsContent = contentFactory.createContent(
            questsPanel.getContent(),
            "📜 Quêtes",
            false
        )
        toolWindow.contentManager.addContent(questsContent)
    }

    override fun shouldBeAvailable(project: Project) = true
}
