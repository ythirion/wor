package com.yot.wor.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class RefactoringActionTypeTest : FunSpec({

    context("RefactoringActionType properties") {
        test("EXTRACT_METHOD should have correct properties") {
            val action = RefactoringActionType.EXTRACT_METHOD

            action.displayName shouldBe "Extract Method"
            action.category shouldBe ActionCategory.STRUCTURE
            action.baseXP shouldBe 10
            action.gameplayTag shouldBe "🧪 Clarity"
        }

        test("REPLACE_CONDITIONAL_WITH_POLYMORPHISM should give high XP") {
            val action = RefactoringActionType.REPLACE_CONDITIONAL_WITH_POLYMORPHISM

            action.baseXP shouldBeGreaterThan 15
            action.category shouldBe ActionCategory.LOGIC
            action.gameplayTag shouldBe "🐍 Hydra Slayer"
        }

        test("all actions should have valid properties") {
            RefactoringActionType.entries.forEach { action ->
                action.displayName.shouldNotBeEmpty()
                action.baseXP shouldBeGreaterThan 0
                action.gameplayTag.shouldNotBeEmpty()
            }
        }
    }

    context("fromIntellijId mapping") {
        test("should map extract method correctly") {
            RefactoringActionType.fromIntellijId("refactoring.extractMethod") shouldBe RefactoringActionType.EXTRACT_METHOD
            RefactoringActionType.fromIntellijId("ExtractMethod") shouldBe RefactoringActionType.EXTRACT_METHOD
        }

        test("should map inline method correctly") {
            RefactoringActionType.fromIntellijId("refactoring.inline.method") shouldBe RefactoringActionType.INLINE_METHOD
        }

        test("should map rename correctly") {
            RefactoringActionType.fromIntellijId("refactoring.rename") shouldBe RefactoringActionType.RENAME
            RefactoringActionType.fromIntellijId("Rename") shouldBe RefactoringActionType.RENAME
        }

        test("should map move method correctly") {
            RefactoringActionType.fromIntellijId("refactoring.move.method") shouldBe RefactoringActionType.MOVE_METHOD
        }

        test("should map move class correctly") {
            RefactoringActionType.fromIntellijId("refactoring.move.class") shouldBe RefactoringActionType.MOVE_CLASS
        }

        test("should return null for unknown refactoring ID") {
            RefactoringActionType.fromIntellijId("unknown.refactoring") shouldBe null
        }

        test("should be case insensitive") {
            RefactoringActionType.fromIntellijId("RENAME") shouldBe RefactoringActionType.RENAME
            RefactoringActionType.fromIntellijId("rename") shouldBe RefactoringActionType.RENAME
            RefactoringActionType.fromIntellijId("ReName") shouldBe RefactoringActionType.RENAME
        }
    }

    context("Action categories distribution") {
        test("should have actions in STRUCTURE category") {
            val structureActions = RefactoringActionType.entries.filter { it.category == ActionCategory.STRUCTURE }
            structureActions.size shouldBeGreaterThan 5
            structureActions shouldContain RefactoringActionType.EXTRACT_METHOD
            structureActions shouldContain RefactoringActionType.RENAME
        }

        test("should have actions in LOGIC category") {
            val logicActions = RefactoringActionType.entries.filter { it.category == ActionCategory.LOGIC }
            logicActions.size shouldBeGreaterThan 3
            logicActions shouldContain RefactoringActionType.SIMPLIFY_BOOLEAN
        }

        test("should have actions in DATA category") {
            val dataActions = RefactoringActionType.entries.filter { it.category == ActionCategory.DATA }
            dataActions.size shouldBeGreaterThan 2
            dataActions shouldContain RefactoringActionType.ENCAPSULATE_FIELD
        }

        test("should have actions in COUPLING category") {
            val couplingActions = RefactoringActionType.entries.filter { it.category == ActionCategory.COUPLING }
            couplingActions.size shouldBeGreaterThan 2
            couplingActions shouldContain RefactoringActionType.INTRODUCE_INTERFACE
        }
    }

    context("XP rewards") {
        test("simple refactorings should give low XP") {
            RefactoringActionType.RENAME.baseXP shouldBe 5
            RefactoringActionType.OPTIMIZE_IMPORTS.baseXP shouldBe 2
        }

        test("complex refactorings should give high XP") {
            RefactoringActionType.BREAK_CYCLIC_DEPENDENCY.baseXP shouldBe 25
            RefactoringActionType.REPLACE_CONDITIONAL_WITH_POLYMORPHISM.baseXP shouldBe 20
        }

        test("all XP values should be positive and reasonable") {
            RefactoringActionType.entries.forEach { action ->
                action.baseXP shouldBeGreaterThan 0
                action.baseXP shouldBe action.baseXP // sanity check
            }
        }
    }
})
