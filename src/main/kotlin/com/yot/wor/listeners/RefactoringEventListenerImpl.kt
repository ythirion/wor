package com.yot.wor.listeners

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.refactoring.listeners.RefactoringEventData
import com.intellij.refactoring.listeners.RefactoringEventListener
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import com.yot.wor.services.RefactoringDetectionService

class RefactoringEventListenerImpl(private val project: Project) : RefactoringEventListener {
    private val detectionService: RefactoringDetectionService by lazy {
        RefactoringDetectionService.getInstance(project)
    }

    override fun refactoringStarted(refactoringId: String, beforeData: RefactoringEventData?) {
        thisLogger().info("Refactoring started: $refactoringId")
    }

    override fun refactoringDone(refactoringId: String, afterData: RefactoringEventData?) {
        // Log with normalized ID to help debug Kotlin-specific refactorings
        val normalizedId = refactoringId.lowercase().replace(".", "_").replace("-", "_")
        thisLogger().info("Refactoring done: $refactoringId (normalized: $normalizedId)")

        val actionType = RefactoringActionType.fromIntellijId(refactoringId)

        if (actionType != null) {
            val fileName = afterData?.getUserData(RefactoringEventData.PSI_ELEMENT_KEY)?.containingFile?.name
            val elementName = afterData?.getUserData(RefactoringEventData.PSI_ELEMENT_KEY)?.text?.take(50)

            val action = RefactoringAction(
                type = actionType,
                fileName = fileName,
                elementName = elementName
            )

            detectionService.onRefactoringDetected(action)
            thisLogger().info("Action detected: ${action.type.displayName} (+${action.xpReward} XP)")
        } else {
            thisLogger().warn("Unknown refactoring ID: $refactoringId")
        }
    }

    override fun conflictsDetected(refactoringId: String, conflictsData: RefactoringEventData) {
        thisLogger().info("Conflicts detected in refactoring: $refactoringId")
    }

    override fun undoRefactoring(refactoringId: String) {
        thisLogger().info("Refactoring undone: $refactoringId")
    }
}
