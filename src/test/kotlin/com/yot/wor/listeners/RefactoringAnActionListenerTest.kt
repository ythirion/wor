package com.yot.wor.listeners

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yot.wor.domain.RefactoringActionType
import com.yot.wor.services.RefactoringDetectionService
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class RefactoringAnActionListenerTest : BasePlatformTestCase() {
    private lateinit var listener: RefactoringAnActionListener
    private lateinit var detectionService: RefactoringDetectionService

    override fun setUp() {
        super.setUp()
        listener = RefactoringAnActionListener()
        detectionService = RefactoringDetectionService.getInstance(project)
        detectionService.reset()
    }

    override fun tearDown() {
        try {
            detectionService.reset()
        } finally {
            super.tearDown()
        }
    }

    fun `test should detect refactoring action`() {
        val action = object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {}
        }

        val actionManager = ActionManager.getInstance()
        actionManager.registerAction("ExtractMethod", action)

        try {
            val event = TestActionEvent.createTestEvent(action) { dataId ->
                when (dataId) {
                    CommonDataKeys.PROJECT.name -> project
                    else -> null
                }
            }
            val result = AnActionResult.PERFORMED

            listener.afterActionPerformed(action, event, result)

            detectionService.allActions() shouldHaveSize 1
            detectionService.allActions().first().type shouldBe RefactoringActionType.EXTRACT_METHOD
        } finally {
            actionManager.unregisterAction("ExtractMethod")
        }
    }

    fun `test should ignore non-refactoring action`() {
        val action = object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {}
        }

        val actionManager = ActionManager.getInstance()
        actionManager.registerAction("EditorCopy", action)

        try {
            val event = TestActionEvent.createTestEvent(action) { dataId ->
                when (dataId) {
                    CommonDataKeys.PROJECT.name -> project
                    else -> null
                }
            }
            val result = AnActionResult.PERFORMED

            listener.afterActionPerformed(action, event, result)

            detectionService.allActions().shouldBeEmpty()
        } finally {
            actionManager.unregisterAction("EditorCopy")
        }
    }

    fun `test should extract file name from action event`() {
        val action = object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {}
        }

        val actionManager = ActionManager.getInstance()
        actionManager.registerAction("Rename", action)

        try {
            val psiFile = myFixture.configureByText("Test.kt", "class Test")

            val event = TestActionEvent.createTestEvent(action) { dataId ->
                when (dataId) {
                    CommonDataKeys.PROJECT.name -> project
                    CommonDataKeys.PSI_FILE.name -> psiFile
                    else -> null
                }
            }
            val result = AnActionResult.PERFORMED

            listener.afterActionPerformed(action, event, result)

            detectionService.allActions() shouldHaveSize 1
            detectionService.allActions().first().fileName shouldBe "Test.kt"
        } finally {
            actionManager.unregisterAction("Rename")
        }
    }

    fun `test should skip refactoring for natively supported languages`() {
        val action = object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {}
        }

        val actionManager = ActionManager.getInstance()
        actionManager.registerAction("ExtractMethod", action)

        try {
            // Create a Java file (natively supported)
            val psiFile = myFixture.configureByText("Test.java", "class Test {}")

            val event = TestActionEvent.createTestEvent(action) { dataId ->
                when (dataId) {
                    CommonDataKeys.PROJECT.name -> project
                    CommonDataKeys.PSI_FILE.name -> psiFile
                    else -> null
                }
            }
            val result = AnActionResult.PERFORMED

            listener.afterActionPerformed(action, event, result)

            // Should be skipped because Java is natively supported
            detectionService.allActions().shouldBeEmpty()
        } finally {
            actionManager.unregisterAction("ExtractMethod")
        }
    }

    fun `test should detect refactoring for Kotlin files`() {
        val action = object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {}
        }

        val actionManager = ActionManager.getInstance()
        actionManager.registerAction("ExtractMethod", action)

        try {
            // Create a Kotlin file (NOT natively supported)
            val psiFile = myFixture.configureByText("Test.kt", "fun test() {}")

            val event = TestActionEvent.createTestEvent(action) { dataId ->
                when (dataId) {
                    CommonDataKeys.PROJECT.name -> project
                    CommonDataKeys.PSI_FILE.name -> psiFile
                    else -> null
                }
            }
            val result = AnActionResult.PERFORMED

            listener.afterActionPerformed(action, event, result)

            // Should be detected because Kotlin is NOT in the natively supported list
            detectionService.allActions() shouldHaveSize 1
            detectionService.allActions().first().type shouldBe RefactoringActionType.EXTRACT_METHOD
        } finally {
            actionManager.unregisterAction("ExtractMethod")
        }
    }
}
