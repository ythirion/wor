package com.yot.craftrpg.listeners

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.refactoring.listeners.RefactoringEventData
import com.intellij.refactoring.listeners.RefactoringEventListener
import com.yot.craftrpg.domain.RefactoringAction
import com.yot.craftrpg.domain.RefactoringActionType
import com.yot.craftrpg.services.RefactoringDetectionService

/**
 * Écoute les événements de refactoring de l'IDE
 */
class RefactoringListener(
    private val detectionService: RefactoringDetectionService
) : RefactoringEventListener {

    override fun refactoringStarted(refactoringId: String, beforeData: RefactoringEventData?) {
        thisLogger().info("Refactoring started: $refactoringId")
    }

    override fun refactoringDone(refactoringId: String, afterData: RefactoringEventData?) {
        thisLogger().info("Refactoring done: $refactoringId")

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
        // TODO: gérer l'annulation (retirer l'XP ?)
    }
}
