package com.yot.craftrpg.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.shouldBe

class QuestTest : FunSpec({

    context("QuestObjective") {
        test("should be completed when count reaches target") {
            val objective = QuestObjective("Do something", targetCount = 5, currentCount = 5)
            objective.isCompleted shouldBe true
        }

        test("should not be completed when count is below target") {
            val objective = QuestObjective("Do something", targetCount = 5, currentCount = 3)
            objective.isCompleted shouldBe false
        }

        test("should calculate progress correctly") {
            val objective = QuestObjective("Do something", targetCount = 10, currentCount = 5)
            objective.progress shouldBe 0.5
        }
    }

    context("Quest") {
        test("should be completed when all objectives are completed") {
            val quest = Quest(
                id = "1",
                title = "Test Quest",
                description = "A test quest",
                category = QuestCategory.REFACTORING,
                xpReward = 100,
                difficulty = QuestDifficulty.EASY,
                objectives = listOf(
                    QuestObjective("Task 1", 5, 5),
                    QuestObjective("Task 2", 3, 3)
                )
            )

            quest.isCompleted shouldBe true
        }

        test("should not be completed when some objectives are incomplete") {
            val quest = Quest(
                id = "1",
                title = "Test Quest",
                description = "A test quest",
                category = QuestCategory.REFACTORING,
                xpReward = 100,
                difficulty = QuestDifficulty.EASY,
                objectives = listOf(
                    QuestObjective("Task 1", 5, 5),
                    QuestObjective("Task 2", 3, 1)
                )
            )

            quest.isCompleted shouldBe false
        }

        test("should calculate overall progress") {
            val quest = Quest(
                id = "1",
                title = "Test Quest",
                description = "A test quest",
                category = QuestCategory.REFACTORING,
                xpReward = 100,
                difficulty = QuestDifficulty.EASY,
                objectives = listOf(
                    QuestObjective("Task 1", 10, 5),
                    QuestObjective("Task 2", 10, 10)
                )
            )

            // 1 objectif complété sur 2 = 50%
            quest.progress shouldBe 0.5
        }
    }

    context("QuestCategory") {
        test("should have correct properties") {
            QuestCategory.REFACTORING.displayName shouldBe "Refactoring"
            QuestCategory.REFACTORING.icon shouldBe "♻️"
        }
    }

    context("QuestDifficulty") {
        test("should have increasing XP multipliers") {
            QuestDifficulty.EASY.xpMultiplier shouldBe 1.0
            QuestDifficulty.MEDIUM.xpMultiplier shouldBe 1.5
            QuestDifficulty.HARD.xpMultiplier shouldBe 2.0
            QuestDifficulty.EXPERT.xpMultiplier shouldBe 3.0
        }

        test("should have appropriate icons") {
            QuestDifficulty.EASY.icon shouldBe "⭐"
            QuestDifficulty.EXPERT.icon shouldBe "⭐⭐⭐⭐"
        }
    }
})
