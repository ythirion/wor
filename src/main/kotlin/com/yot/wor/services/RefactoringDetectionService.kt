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

    // Deduplication: cache of recent actions to avoid counting the same refactoring twice
    // (e.g., from both RefactoringEventListener and CommandListener)
    private val recentActionsCache = mutableMapOf<String, Long>()
    private val deduplicationWindowMs = 500L // 500ms window to detect duplicates

    fun interface RefactoringActionListener {
        fun onActionDetected(action: RefactoringAction)
    }

    fun onRefactoringDetected(action: RefactoringAction) {
        // Deduplication: check if we've seen this action type recently
        val cacheKey = "${action.type.name}_${action.fileName ?: "unknown"}"
        val now = System.currentTimeMillis()

        synchronized(recentActionsCache) {
            val lastSeen = recentActionsCache[cacheKey]
            if (lastSeen != null && (now - lastSeen) < deduplicationWindowMs) {
                thisLogger().info("Skipping duplicate refactoring: ${action.type.displayName}")
                return
            }
            recentActionsCache[cacheKey] = now

            // Clean up old entries (keep cache small)
            val cutoff = now - deduplicationWindowMs
            recentActionsCache.entries.removeIf { it.value < cutoff }
        }

        thisLogger().info("Refactoring detected: ${action.type.displayName} (+${action.xpReward} XP)")
        detectedActions.add(action)
        notifyListeners(action)

        // Notify global services
        PlayerStateService.getInstance().addRefactoringAction(action, project)
        QuestService.getInstance().updateQuestProgress(action)
    }

    fun addListener(listener: RefactoringActionListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: RefactoringActionListener) {
        listeners.remove(listener)
    }

    fun allActions(): List<RefactoringAction> = detectedActions.toList()
    fun recentActions(count: Int): List<RefactoringAction> = detectedActions.takeLast(count)
    fun totalXP(): Int = detectedActions.sumOf { it.xpReward }
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
