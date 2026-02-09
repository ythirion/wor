package com.yot.craftrpg.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.time.Instant

class PlayerStateTest : FunSpec({
    context("Level calculation") {
        test("calculateXPForLevel should return 0 for level 1") {
            PlayerState.calculateXPForLevel(1) shouldBe 0
        }

        test("calculateXPForLevel should increase with level") {
            val level2XP = PlayerState.calculateXPForLevel(2)
            val level3XP = PlayerState.calculateXPForLevel(3)
            val level5XP = PlayerState.calculateXPForLevel(5)

            level2XP shouldBeGreaterThan 0
            level3XP shouldBeGreaterThan level2XP
            level5XP shouldBeGreaterThan level3XP
        }

        test("calculateLevel should return correct level for given XP") {
            PlayerState.calculateLevel(0) shouldBe 1
            PlayerState.calculateLevel(50) shouldBe 1
            PlayerState.calculateLevel(300) shouldBe 2
            PlayerState.calculateLevel(1000) shouldBe 4
        }

        test("level and XP calculation should be consistent") {
            for (level in 1..10) {
                val xpForLevel = PlayerState.calculateXPForLevel(level)
                val calculatedLevel = PlayerState.calculateLevel(xpForLevel)
                calculatedLevel shouldBe level
            }
        }
    }

    context("PlayerState properties") {
        test("new player should start at level 1 with 0 XP") {
            val state = PlayerState()

            state.level shouldBe 1
            state.totalXP shouldBe 0
            state.actionsHistory shouldBe emptyList()
        }

        test("xpForNextLevel should be calculated correctly") {
            val state = PlayerState(totalXP = 150, level = 2)

            state.xpForNextLevel shouldBe PlayerState.calculateXPForLevel(3)
        }

        test("currentLevelXP should be XP within current level") {
            val level2XP = PlayerState.calculateXPForLevel(2)
            val state = PlayerState(totalXP = level2XP + 50, level = 2)

            state.currentLevelXP shouldBe 50
        }

        test("levelProgress should be between 0 and 1") {
            val level2XP = PlayerState.calculateXPForLevel(2)
            val state = PlayerState(totalXP = level2XP + 50, level = 2)

            state.levelProgress shouldBe(0.2109704641350211)
        }

        test("levelProgress should be 0 at start of level") {
            val level2XP = PlayerState.calculateXPForLevel(2)
            val state = PlayerState(totalXP = level2XP, level = 2)

            state.levelProgress shouldBe 0.0
        }
    }

    context("Player titles") {
        test("low level should have apprentice title") {
            val state = PlayerState(level = 1)
            state.title shouldBe "Refactoring Apprentice"
            state.levelIcon shouldBe "🌱"
        }

        test("mid level should have appropriate title") {
            val state1 = PlayerState(level = 5)
            state1.title shouldBe "Refactorer"
            state1.levelIcon shouldBe "⚔️"

            val state2 = PlayerState(level = 10)
            state2.title shouldBe "Expert Refactorer"
            state2.levelIcon shouldBe "🛡️"
        }

        test("high level should have master title") {
            val state1 = PlayerState(level = 25)
            state1.title shouldBe "Refactoring Master"
            state1.levelIcon shouldBe "🎖️"

            val state2 = PlayerState(level = 50)
            state2.title shouldBe "Grand Master"
            state2.levelIcon shouldBe "👑"
        }

        test("very high level should have legend title") {
            val state = PlayerState(level = 100)
            state.title shouldBe "Living Legend"
            state.levelIcon shouldBe "🧙"
        }
    }

    context("CategoryStats") {
        test("should calculate average XP correctly") {
            val stats = CategoryStats(
                category = ActionCategory.STRUCTURE,
                actionCount = 5,
                totalXP = 50
            )

            stats.averageXP shouldBe 10.0
        }

        test("should return 0 average for no actions") {
            val stats = CategoryStats(
                category = ActionCategory.STRUCTURE,
                actionCount = 0,
                totalXP = 0
            )

            stats.averageXP shouldBe 0.0
        }

        test("should track most used action") {
            val stats = CategoryStats(
                category = ActionCategory.STRUCTURE,
                actionCount = 10,
                totalXP = 100,
                mostUsedAction = RefactoringActionType.RENAME
            )

            stats.mostUsedAction shouldBe RefactoringActionType.RENAME
        }
    }

    context("PlayerState with history") {
        test("should track actions history") {
            val action1 = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
            val action2 = RefactoringAction(type = RefactoringActionType.RENAME)

            val state = PlayerState(
                totalXP = 15,
                level = 1,
                actionsHistory = listOf(action1, action2)
            )

            state.actionsHistory.size shouldBe 2
            state.actionsHistory[0] shouldBe action1
            state.actionsHistory[1] shouldBe action2
        }

        test("should track last action timestamp") {
            val timestamp = Instant.now()
            val action = RefactoringAction(
                type = RefactoringActionType.EXTRACT_METHOD,
                timestamp = timestamp
            )

            val state = PlayerState(
                actionsHistory = listOf(action),
                lastActionTimestamp = timestamp
            )

            state.lastActionTimestamp shouldBe timestamp
        }
    }

    context("Statistics by category") {
        test("should have stats for all categories") {
            val stats = mapOf(
                ActionCategory.STRUCTURE to CategoryStats(ActionCategory.STRUCTURE, 5, 50),
                ActionCategory.LOGIC to CategoryStats(ActionCategory.LOGIC, 3, 30),
                ActionCategory.DATA to CategoryStats(ActionCategory.DATA, 2, 20),
                ActionCategory.COUPLING to CategoryStats(ActionCategory.COUPLING, 1, 10)
            )

            val state = PlayerState(statisticsByCategory = stats)

            state.statisticsByCategory.size shouldBe 4
            state.statisticsByCategory[ActionCategory.STRUCTURE]?.actionCount shouldBe 5
        }
    }
})
