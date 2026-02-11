package com.yot.wor.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class WorToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        val statsPanel = WorToolWindow(project)
        val statsContent = contentFactory.createContent(
            statsPanel.getContent(),
            "📊 Stats",
            false
        )
        toolWindow.contentManager.addContent(statsContent)

        val questsPanel = QuestsPanel()
        val questsContent = contentFactory.createContent(
            questsPanel.getContent(),
            "📜 Quests",
            false
        )
        toolWindow.contentManager.addContent(questsContent)
    }

    override fun shouldBeAvailable(project: Project) = true
}
