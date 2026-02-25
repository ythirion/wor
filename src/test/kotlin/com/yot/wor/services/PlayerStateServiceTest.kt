package com.yot.wor.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yot.wor.domain.ActionCategory
import com.yot.wor.domain.RefactoringAction
import com.yot.wor.domain.RefactoringActionType
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class PlayerStateServiceTest : BasePlatformTestCase() {
    private lateinit var service: PlayerStateService

    override fun setUp() {
        super.setUp()
        service = PlayerStateService.getInstance()
        service.reset()
    }

    override fun tearDown() {
        try {
            service.reset()
        } finally {
            super.tearDown()
        }
    }

    fun `test new player should start with default state`() {
        val state = service.playerState()

        state.level shouldBe 1
        state.totalXP shouldBe 0
        state.actionsHistory.shouldBeEmpty()
    }

    fun `test addRefactoringAction should increase XP`() {
        val action = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)

        service.addRefactoringAction(action, project)

        val state = service.playerState()
        state.totalXP shouldBe 10
        state.actionsHistory shouldHaveSize 1
    }

    fun `test multiple actions should accumulate XP`() {
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD), project) // 10 XP
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project) // 5 XP
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.MOVE_METHOD), project) // 12 XP

        val state = service.playerState()
        state.totalXP shouldBe 27
        state.actionsHistory shouldHaveSize 3
    }

    fun `test should level up when reaching XP threshold`() {
        repeat(20) {
            service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.MOVE_METHOD), project) // 12 XP each
        }

        val state = service.playerState()
        state.totalXP shouldBeGreaterThan 200
        state.level shouldBeGreaterThan 1
    }

    fun `test listener should be notified of state changes`() {
        var notificationCount = 0
        var lastState: com.yot.wor.domain.PlayerState? = null

        val listener = PlayerStateService.PlayerStateListener { state ->
            notificationCount++
            lastState = state
        }

        service.addListener(listener)

        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)

        notificationCount shouldBe 1
        lastState shouldNotBe null
        lastState?.totalXP shouldBe 5
    }

    fun `test multiple listeners should all be notified`() {
        var count1 = 0
        var count2 = 0

        service.addListener { count1++ }
        service.addListener { count2++ }

        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)

        count1 shouldBe 1
        count2 shouldBe 1
    }

    fun `test removeListener should stop notifications`() {
        var count = 0
        val listener = PlayerStateService.PlayerStateListener { count++ }

        service.addListener(listener)
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)
        count shouldBe 1

        service.removeListener(listener)
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)
        count shouldBe 1 // Should still be 1
    }

    fun `test should calculate category statistics`() {
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD), project)
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.REMOVE_DEAD_CODE), project)

        val state = service.playerState()
        val structureStats = state.statisticsByCategory[ActionCategory.STRUCTURE]
        val logicStats = state.statisticsByCategory[ActionCategory.LOGIC]

        structureStats shouldNotBe null
        structureStats?.actionCount shouldBe 2 // EXTRACT_METHOD + RENAME

        logicStats shouldNotBe null
        logicStats?.actionCount shouldBe 1 // REMOVE_DEAD_CODE
    }

    fun `test should track most used action per category`() {
        repeat(5) {
            service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)
        }
        repeat(2) {
            service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD), project)
        }

        val state = service.playerState()
        val structureStats = state.statisticsByCategory[ActionCategory.STRUCTURE]

        structureStats?.mostUsedAction shouldBe RefactoringActionType.RENAME
    }

    fun `test should track last action timestamp`() {
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)
        val state1 = service.playerState()

        Thread.sleep(10) // Petit délai pour différencier les timestamps

        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD), project)
        val state2 = service.playerState()

        state1.lastActionTimestamp shouldNotBe null
        state2.lastActionTimestamp shouldNotBe null
        (state2.lastActionTimestamp!! > state1.lastActionTimestamp!!) shouldBe true
    }

    fun `test reset should clear all state`() {
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD), project)
        service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)

        service.playerState().totalXP shouldBeGreaterThan 0

        service.reset()

        val state = service.playerState()
        state.totalXP shouldBe 0
        state.level shouldBe 1
        state.actionsHistory.shouldBeEmpty()
    }

    fun `test should handle rapid successive actions`() {
        repeat(50) {
            service.addRefactoringAction(RefactoringAction(type = RefactoringActionType.RENAME), project)
        }

        val state = service.playerState()
        state.actionsHistory shouldHaveSize 50
        state.totalXP shouldBe 250 // 50 * 5
    }

    fun `test actions history should be in chronological order`() {
        val action1 = RefactoringAction(type = RefactoringActionType.RENAME, fileName = "File1.kt")
        Thread.sleep(5)
        val action2 = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD, fileName = "File2.kt")
        Thread.sleep(5)
        val action3 = RefactoringAction(type = RefactoringActionType.REMOVE_DEAD_CODE, fileName = "File3.kt")

        service.addRefactoringAction(action1, project)
        service.addRefactoringAction(action2, project)
        service.addRefactoringAction(action3, project)

        val state = service.playerState()
        val history = state.actionsHistory

        history shouldHaveSize 3
        history[0].fileName shouldBe "File1.kt"
        history[1].fileName shouldBe "File2.kt"
        history[2].fileName shouldBe "File3.kt"
    }

    fun `test getInstance should return same instance`() {
        val service1 = PlayerStateService.getInstance()
        val service2 = PlayerStateService.getInstance()

        service1 shouldBe service2
    }
}
