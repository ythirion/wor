package com.yot.wor.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yot.wor.domain.ActionCategory
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class RefactoringDetectionServiceTest : BasePlatformTestCase() {
    private lateinit var service: RefactoringDetectionService

    override fun setUp() {
        super.setUp()
        service = RefactoringDetectionService.getInstance(project)
        service.reset()
    }

    override fun tearDown() {
        try {
            service.reset()
        } finally {
            super.tearDown()
        }
    }

    fun `test service should start with no actions`() {
        service.allActions().shouldBeEmpty()
        service.totalXP() shouldBe 0
    }

    fun `test onRefactoringDetected should add action`() {
        val action = RefactoringAction(
            type = RefactoringActionType.EXTRACT_METHOD,
            fileName = "Test.kt"
        )

        service.onRefactoringDetected(action)

        service.allActions() shouldHaveSize 1
        service.allActions().first() shouldBe action
    }

    fun `test should calculate total XP correctly`() {
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)) // 10 XP
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME)) // 5 XP
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.EXTRACT_CLASS)) // 15 XP

        service.totalXP() shouldBe 30
    }

    fun `test getRecentActions should return last N actions`() {
        repeat(10) { i ->
            service.onRefactoringDetected(
                RefactoringAction(
                    type = RefactoringActionType.RENAME,
                    fileName = "File$i.kt"
                )
            )
        }

        val recent = service.recentActions(3)
        recent shouldHaveSize 3
        recent[0].fileName shouldBe "File7.kt"
        recent[1].fileName shouldBe "File8.kt"
        recent[2].fileName shouldBe "File9.kt"
    }

    fun `test listener should be notified of actions`() {
        var notificationCount = 0
        var lastAction: RefactoringAction? = null

        val listener = RefactoringDetectionService.RefactoringActionListener { action ->
            notificationCount++
            lastAction = action
        }

        service.addListener(listener)

        val action = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
        service.onRefactoringDetected(action)

        notificationCount shouldBe 1
        lastAction shouldBe action
    }

    fun `test multiple listeners should all be notified`() {
        var count1 = 0
        var count2 = 0

        service.addListener { count1++ }
        service.addListener { count2++ }

        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME))

        count1 shouldBe 1
        count2 shouldBe 1
    }

    fun `test removeListener should stop notifications`() {
        var count = 0
        val listener = RefactoringDetectionService.RefactoringActionListener { count++ }

        service.addListener(listener)
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME))
        count shouldBe 1

        service.removeListener(listener)
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME))
        count shouldBe 1 // Should still be 1, not 2
    }

    fun `test reset should clear all actions`() {
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD))
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME))

        service.allActions() shouldHaveSize 2
        service.totalXP() shouldBeGreaterThan 0

        service.reset()

        service.allActions().shouldBeEmpty()
        service.totalXP() shouldBe 0
    }

    fun `test should track multiple action types`() {
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD))
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME))
        service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.SIMPLIFY_BOOLEAN))

        val actions = service.allActions()
        actions shouldHaveSize 3

        val categories = actions.map { it.category }.toSet()
        categories shouldContain ActionCategory.STRUCTURE
        categories shouldContain ActionCategory.LOGIC
    }

    fun `test should handle rapid successive actions`() {
        repeat(100) {
            service.onRefactoringDetected(RefactoringAction(type = RefactoringActionType.RENAME))
        }

        service.allActions() shouldHaveSize 100
        service.totalXP() shouldBe 500 // 100 * 5 XP
    }

    fun `test getInstance should return same instance`() {
        val service1 = RefactoringDetectionService.getInstance(project)
        val service2 = RefactoringDetectionService.getInstance(project)

        service1 shouldBe service2
    }
}
