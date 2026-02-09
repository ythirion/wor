package com.yot.wor.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.yot.wor.domain.RefactoringAction
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Service pour détecter et gérer les actions de refactoring
 */
@Service(Service.Level.PROJECT)
class RefactoringDetectionService(private val project: Project) {

    private val detectedActions = CopyOnWriteArrayList<RefactoringAction>()
    private val listeners = CopyOnWriteArrayList<RefactoringActionListener>()

    /**
     * Interface pour écouter les actions détectées
     */
    fun interface RefactoringActionListener {
        fun onActionDetected(action: RefactoringAction)
    }

    /**
     * Enregistre une action de refactoring détectée
     */
    fun onRefactoringDetected(action: RefactoringAction) {
        thisLogger().info("Refactoring detected: ${action.type.displayName} (+${action.xpReward} XP)")
        detectedActions.add(action)
        notifyListeners(action)
    }

    /**
     * Ajoute un listener pour être notifié des actions
     */
    fun addListener(listener: RefactoringActionListener) {
        listeners.add(listener)
    }

    /**
     * Retire un listener
     */
    fun removeListener(listener: RefactoringActionListener) {
        listeners.remove(listener)
    }

    /**
     * Récupère toutes les actions détectées
     */
    fun getAllActions(): List<RefactoringAction> = detectedActions.toList()

    /**
     * Récupère les N dernières actions
     */
    fun getRecentActions(count: Int): List<RefactoringAction> =
        detectedActions.takeLast(count)

    /**
     * Calcule l'XP total gagné
     */
    fun getTotalXP(): Int = detectedActions.sumOf { it.xpReward }

    /**
     * Réinitialise les statistiques (pour les tests)
     */
    fun reset() {
        detectedActions.clear()
    }

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
