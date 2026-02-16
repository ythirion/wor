package com.yot.wor.listeners

import com.intellij.openapi.command.CommandEvent
import com.intellij.openapi.command.CommandListener
import com.intellij.openapi.diagnostic.thisLogger
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import com.yot.wor.services.RefactoringDetectionService

class RefactoringCommandListener : CommandListener {
    override fun commandFinished(event: CommandEvent) {
        val commandName = event.commandName ?: return
        val project = event.project ?: return

        thisLogger().info("Command finished: $commandName")

        val actionType = RefactoringActionType.fromIntellijId(commandName)

        if (actionType != null) {
            val refactoringAction = RefactoringAction(
                type = actionType,
                fileName = null, // Could be extracted from event if needed
                elementName = null
            )

            val detectionService = RefactoringDetectionService.getInstance(project)
            detectionService.onRefactoringDetected(refactoringAction)
            thisLogger().info("Command detected: ${refactoringAction.type.displayName} (+${refactoringAction.xpReward} XP)")
        } else {
            // Log unknown refactoring commands for debugging
            if (commandName.contains("extract", ignoreCase = true) ||
                commandName.contains("refactor", ignoreCase = true) ||
                commandName.contains("inline", ignoreCase = true) ||
                commandName.contains("introduce", ignoreCase = true) ||
                commandName.contains("rename", ignoreCase = true)) {
                thisLogger().warn("Unknown refactoring command: $commandName")
            }
        }
    }
}
