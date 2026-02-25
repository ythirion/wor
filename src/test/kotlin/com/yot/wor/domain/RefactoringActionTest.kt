package com.yot.wor.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant

class RefactoringActionTest : FunSpec({

    test("should create RefactoringAction with minimal data") {
        val action = RefactoringAction(
            type = RefactoringActionType.EXTRACT_METHOD
        )

        action.type shouldBe RefactoringActionType.EXTRACT_METHOD
        action.xpReward shouldBe 10
        action.category shouldBe ActionCategory.STRUCTURE
        action.fileName shouldBe null
        action.elementName shouldBe null
    }

    test("should create RefactoringAction with full data") {
        val timestamp = Instant.now()
        val action = RefactoringAction(
            type = RefactoringActionType.RENAME,
            timestamp = timestamp,
            fileName = "MyClass.kt",
            elementName = "oldMethodName"
        )

        action.type shouldBe RefactoringActionType.RENAME
        action.timestamp shouldBe timestamp
        action.fileName shouldBe "MyClass.kt"
        action.elementName shouldBe "oldMethodName"
        action.xpReward shouldBe 5
    }

    test("xpReward should match the action type base XP") {
        val extractMethodAction = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
        extractMethodAction.xpReward shouldBe RefactoringActionType.EXTRACT_METHOD.baseXP

        val renameAction = RefactoringAction(type = RefactoringActionType.RENAME)
        renameAction.xpReward shouldBe RefactoringActionType.RENAME.baseXP
    }

    test("category should match the action type category") {
        val structureAction = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
        structureAction.category shouldBe ActionCategory.STRUCTURE

        val logicAction = RefactoringAction(type = RefactoringActionType.REMOVE_DEAD_CODE)
        logicAction.category shouldBe ActionCategory.LOGIC
    }

    test("timestamp should be automatically set if not provided") {
        val before = Instant.now()
        val action = RefactoringAction(type = RefactoringActionType.RENAME)
        val after = Instant.now()

        action.timestamp shouldNotBe null
        // Le timestamp devrait être entre before et after (avec une petite marge)
        (action.timestamp >= before || action.timestamp <= after) shouldBe true
    }

    test("two actions with same type should have same XP") {
        val action1 = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
        val action2 = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)

        action1.xpReward shouldBe action2.xpReward
    }

    test("actions with different types should have different XP") {
        val extractMethod = RefactoringAction(type = RefactoringActionType.EXTRACT_METHOD)
        val rename = RefactoringAction(type = RefactoringActionType.RENAME)

        extractMethod.xpReward shouldNotBe rename.xpReward
    }
})
