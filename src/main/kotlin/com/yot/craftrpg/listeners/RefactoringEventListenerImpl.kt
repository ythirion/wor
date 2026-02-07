package com.yot.craftrpg.listeners

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.refactoring.listeners.RefactoringEventData
import com.intellij.refactoring.listeners.RefactoringEventListener
import com.yot.craftrpg.domain.RefactoringAction
import com.yot.craftrpg.domain.RefactoringActionType
import com.yot.craftrpg.services.RefactoringDetectionService

/**
 * Implémentation du listener pour les événements de refactoring
 * Enregistré via plugin.xml comme projectListener
 */
class RefactoringEventListenerImpl(private val project: Project) : RefactoringEventListener {

    private val detectionService: RefactoringDetectionService by lazy {
        RefactoringDetectionService.getInstance(project)
    }

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
