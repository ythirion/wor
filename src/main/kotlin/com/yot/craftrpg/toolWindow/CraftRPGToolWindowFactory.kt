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
        val craftRPGToolWindow = CraftRPGToolWindow(project)
        val content = ContentFactory.getInstance().createContent(
            craftRPGToolWindow.getContent(),
            "Stats",
            false
        )
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
