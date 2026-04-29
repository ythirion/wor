package com.yot.wor.listeners

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.vfs.VirtualFile
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import com.yot.wor.services.RefactoringDetectionService

class RefactoringAnActionListener : AnActionListener {
    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        val project = event.project ?: return
        val actionId = event.actionManager.getId(action) ?: return

        thisLogger().debug("Action performed: $actionId")

        val actionType = RefactoringActionType.fromIntellijId(actionId) ?: run {
            if (actionId.contains("extract", ignoreCase = true) ||
                actionId.contains("refactor", ignoreCase = true) ||
                actionId.contains("inline", ignoreCase = true) ||
                actionId.contains("introduce", ignoreCase = true) ||
                actionId.contains("rename", ignoreCase = true)
            ) {
                thisLogger().warn("Unknown refactoring action ID: $actionId")
            }
            return
        }

        val virtualFile = ReadAction.compute<VirtualFile?, RuntimeException> { event.getData(CommonDataKeys.VIRTUAL_FILE) }
        val elementName = ReadAction.compute<String?, RuntimeException> { event.getData(CommonDataKeys.PSI_ELEMENT)?.text?.take(50) }

        val refactoringAction = RefactoringAction(
            type = actionType,
            fileName = virtualFile?.name,
            elementName = elementName
        )

        RefactoringDetectionService.getInstance(project).onRefactoringDetected(refactoringAction)
        thisLogger().info("Action detected: ${refactoringAction.type.displayName} in ${virtualFile?.name ?: "unknown"} (+${refactoringAction.xpReward} XP)")
    }
}
