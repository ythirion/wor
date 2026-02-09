package com.yot.wor.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.yot.wor.domain.RefactoringAction
import java.util.concurrent.CopyOnWriteArrayList

@Service(Service.Level.PROJECT)
class RefactoringDetectionService(private val project: Project) {

    private val detectedActions = CopyOnWriteArrayList<RefactoringAction>()
    private val listeners = CopyOnWriteArrayList<RefactoringActionListener>()

    fun interface RefactoringActionListener {
        fun onActionDetected(action: RefactoringAction)
    }

    fun onRefactoringDetected(action: RefactoringAction) {
        thisLogger().info("Refactoring detected: ${action.type.displayName} (+${action.xpReward} XP)")
        detectedActions.add(action)
        notifyListeners(action)
    }

    fun addListener(listener: RefactoringActionListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: RefactoringActionListener) {
        listeners.remove(listener)
    }

    fun getAllActions(): List<RefactoringAction> = detectedActions.toList()
    fun getRecentActions(count: Int): List<RefactoringAction> = detectedActions.takeLast(count)
    fun getTotalXP(): Int = detectedActions.sumOf { it.xpReward }
    fun reset() = detectedActions.clear()

    private fun notifyListeners(action: RefactoringAction) {
        listeners.forEach { listener ->
            try {
                listener.onActionDetected(action)
            } catch (e: Exception) {
                thisLogger().error("Error notifying listener", e)
            }
        }
    }

    companion object {
        fun getInstance(project: Project): RefactoringDetectionService =
            project.getService(RefactoringDetectionService::class.java)
    }
}
