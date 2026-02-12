package com.yot.wor.services

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yot.wor.domain.*
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.*

class QuestServiceTest : BasePlatformTestCase() {
    private lateinit var service: QuestService

    override fun setUp() {
        super.setUp()
        service = QuestService.getInstance()
    }

    fun `test should have starter quests`() {
        val quests = service.activeQuests()
        quests.size shouldBeGreaterThan 0
    }

    fun `test should add new quest`() {
        val initialCount = service.activeQuests().size

        val quest = Quest(
            id = "test-quest",
            title = "Test Quest",
            description = "A test quest",
            category = QuestCategory.REFACTORING,
            xpReward = 100,
            difficulty = QuestDifficulty.EASY,
            objectives = listOf(
                QuestObjective("Do something", 5)
            )
        )

        service.addQuest(quest)

        service.activeQuests() shouldHaveSize (initialCount + 1)
        service.activeQuests() shouldContain quest
    }

    fun `test should filter quests by category`() {
        val quest = Quest(
            id = "cleanup-quest",
            title = "Cleanup Quest",
            description = "A cleanup quest",
            category = QuestCategory.CLEANUP,
            xpReward = 100,
            difficulty = QuestDifficulty.EASY,
            objectives = listOf(QuestObjective("Clean", 1))
        )

        service.addQuest(quest)

        val cleanupQuests = service.questsByCategory(QuestCategory.CLEANUP)
        cleanupQuests shouldContain quest
    }

    fun `test should filter quests by difficulty`() {
        val quest = Quest(
            id = "hard-quest",
            title = "Hard Quest",
            description = "A hard quest",
            category = QuestCategory.REFACTORING,
            xpReward = 500,
            difficulty = QuestDifficulty.HARD,
            objectives = listOf(QuestObjective("Do hard thing", 10))
        )

        service.addQuest(quest)

        val hardQuests = service.questsByDifficulty(QuestDifficulty.HARD)
        hardQuests shouldContain quest
    }

    fun `test listener should be notified`() {
        var notified = false

        service.addListener {
            notified = true
        }

        val quest = Quest(
            id = "notify-quest",
            title = "Notify Quest",
            description = "A quest to test notifications",
            category = QuestCategory.REFACTORING,
            xpReward = 100,
            difficulty = QuestDifficulty.EASY,
            objectives = listOf(QuestObjective("Notify", 1))
        )

        service.addQuest(quest)

        notified shouldBe true
    }

    fun `test getInstance should return same instance`() {
        val service1 = QuestService.getInstance()
        val service2 = QuestService.getInstance()

        service1 shouldBe service2
    }

    fun `test should complete Extract Expert quest after 5 extract method actions`() {
        val extractExpertQuest = Quest(
            id = UUID.randomUUID().toString(),
            title = "Extract Expert",
            description = "Extract 5 methods to improve readability",
            category = QuestCategory.REFACTORING,
            xpReward = 200,
            difficulty = QuestDifficulty.MEDIUM,
            objectives = listOf(
                QuestObjective("Extract Method × 5", targetCount = 5)
            )
        )

        service.addQuest(extractExpertQuest)

        service.activeQuests() shouldContain extractExpertQuest
        service.completedQuests() shouldNotContain extractExpertQuest

        repeat(5) {
            service.updateQuestProgress(
                RefactoringAction(
                    type = RefactoringActionType.EXTRACT_METHOD,
                    fileName = "TestFile.kt",
                    elementName = "extractedMethod${it + 1}"
                )
            )
        }

        service.activeQuests().none { it.id == extractExpertQuest.id } shouldBe true

        val completedQuest = service.completedQuests().find { it.id == extractExpertQuest.id }
        completedQuest shouldNotBe null
        completedQuest!!.isCompleted shouldBe true
        completedQuest.status shouldBe QuestStatus.COMPLETED
        completedQuest.completedAt shouldNotBe null
        completedQuest.objectives.all { it.isCompleted } shouldBe true
        completedQuest.objectives[0].currentCount shouldBe 5
    }
}
