package com.yot.wor.listeners

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.diagnostic.thisLogger
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import com.yot.wor.services.RefactoringDetectionService

class RefactoringAnActionListener : AnActionListener {
    companion object {
        // Languages natively supported by RefactoringEventListener
        // For these languages, we skip AnActionListener detection to avoid duplicates
        // Note: Some Kotlin refactorings (e.g., Change Signature) use the standard API
        // and may still be detected by both listeners. A lightweight deduplication in
        // RefactoringDetectionService handles these edge cases.
        private val NATIVELY_SUPPORTED_LANGUAGES = setOf(
            "JAVA",
            "JavaScript",
            "TypeScript",
            "Python",
            "PHP",
            "Ruby",
            "Go",
            "HTML",
            "CSS",
            "XML"
        )

        private val NATIVELY_SUPPORTED_EXTENSIONS = setOf(
            "java",
            "js", "jsx", "ts", "tsx",
            "py",
            "php",
            "rb",
            "go",
            "html", "htm",
            "css", "scss", "sass",
            "xml"
        )
    }

    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        val project = event.project ?: return
        val actionId = event.actionManager.getId(action) ?: return

        thisLogger().debug("Action performed: $actionId")

        // Check if this is a natively supported language
        // If yes, RefactoringEventListener will handle it
        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)

        val fileExtension = virtualFile?.extension
        val languageName = psiFile?.language?.displayName

        if (fileExtension != null && fileExtension in NATIVELY_SUPPORTED_EXTENSIONS) {
            thisLogger().debug("Skipping $actionId for .$fileExtension file (natively supported by RefactoringEventListener)")
            return
        }

        if (languageName != null && languageName in NATIVELY_SUPPORTED_LANGUAGES) {
            thisLogger().debug("Skipping $actionId for $languageName (natively supported by RefactoringEventListener)")
            return
        }

        // Try to map the action ID to a refactoring type
        val actionType = RefactoringActionType.fromIntellijId(actionId)

        if (actionType != null) {
            val fileName = virtualFile?.name ?: psiFile?.name

            val elementName = event.getData(CommonDataKeys.PSI_ELEMENT)?.text?.take(50)

            val refactoringAction = RefactoringAction(
                type = actionType,
                fileName = fileName,
                elementName = elementName
            )

            val detectionService = RefactoringDetectionService.getInstance(project)
            detectionService.onRefactoringDetected(refactoringAction)

            thisLogger().info("Action detected via AnActionListener: ${refactoringAction.type.displayName} in ${fileName ?: "unknown"} (${languageName ?: fileExtension ?: "unknown language"}) (+${refactoringAction.xpReward} XP)")
        } else
            // Log unknown refactoring actions for debugging
            if (actionId.contains("extract", ignoreCase = true) ||
                actionId.contains("refactor", ignoreCase = true) ||
                actionId.contains("inline", ignoreCase = true) ||
                actionId.contains("introduce", ignoreCase = true) ||
                actionId.contains("rename", ignoreCase = true)) {
                thisLogger().warn("Unknown refactoring action ID: $actionId (language: ${languageName ?: fileExtension ?: "unknown"})")
            }
    }
}
