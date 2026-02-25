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

        test("all actions should have valid properties") {
            RefactoringActionType.entries.forEach { action ->
                action.displayName.shouldNotBeEmpty()
                action.baseXP shouldBeGreaterThan 0
                action.gameplayTag.shouldNotBeEmpty()
            }
        }
    }

    context("fromIntellijId mapping") {
        context("exact WebStorm IDs") {
            test("refactoring.javascript.extractMethod -> EXTRACT_METHOD") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.extractMethod") shouldBe RefactoringActionType.EXTRACT_METHOD
            }
            test("refactoring.javascript.inline.method -> INLINE_METHOD") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.inline.method") shouldBe RefactoringActionType.INLINE_METHOD
            }
            test("refactoring.javascript.inline -> INLINE_VARIABLE") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.inline") shouldBe RefactoringActionType.INLINE_VARIABLE
            }
            test("refactoring.javascript.es6.moveModule -> MOVE_METHOD") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.es6.moveModule") shouldBe RefactoringActionType.MOVE_METHOD
            }
            test("refactoring.inplace.rename -> RENAME") {
                RefactoringActionType.fromIntellijId("refactoring.inplace.rename") shouldBe RefactoringActionType.RENAME
            }
            test("refactoring.rename -> RENAME") {
                RefactoringActionType.fromIntellijId("refactoring.rename") shouldBe RefactoringActionType.RENAME
            }
            test("refactoring.javascript.change.signature -> CHANGE_SIGNATURE") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.change.signature") shouldBe RefactoringActionType.CHANGE_SIGNATURE
            }
            test("refactoring.safeDelete -> SAFE_DELETE") {
                RefactoringActionType.fromIntellijId("refactoring.safeDelete") shouldBe RefactoringActionType.SAFE_DELETE
            }
            test("refactoring.javascript.introduceVariable -> EXTRACT_VARIABLE") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.introduceVariable") shouldBe RefactoringActionType.EXTRACT_VARIABLE
            }
            test("refactoring.javascript.introduceConstant -> EXTRACT_CONSTANT") {
                RefactoringActionType.fromIntellijId("refactoring.javascript.introduceConstant") shouldBe RefactoringActionType.EXTRACT_CONSTANT
            }
        }

        context("exact IntelliJ IDEA IDs") {
            test("refactoring.extract.method -> EXTRACT_METHOD") {
                RefactoringActionType.fromIntellijId("refactoring.extract.method") shouldBe RefactoringActionType.EXTRACT_METHOD
            }
            test("refactoring.inline.method -> INLINE_METHOD") {
                RefactoringActionType.fromIntellijId("refactoring.inline.method") shouldBe RefactoringActionType.INLINE_METHOD
            }
            test("refactoring.inline.local.variable -> INLINE_VARIABLE") {
                RefactoringActionType.fromIntellijId("refactoring.inline.local.variable") shouldBe RefactoringActionType.INLINE_VARIABLE
            }
            test("refactoring.move.members -> MOVE_METHOD") {
                RefactoringActionType.fromIntellijId("refactoring.move.members") shouldBe RefactoringActionType.MOVE_METHOD
            }
            test("refactoring.changeSignature -> CHANGE_SIGNATURE") {
                RefactoringActionType.fromIntellijId("refactoring.changeSignature") shouldBe RefactoringActionType.CHANGE_SIGNATURE
            }
            test("refactoring.extractVariable -> EXTRACT_VARIABLE") {
                RefactoringActionType.fromIntellijId("refactoring.extractVariable") shouldBe RefactoringActionType.EXTRACT_VARIABLE
            }
            test("refactoring.extractConstant -> EXTRACT_CONSTANT") {
                RefactoringActionType.fromIntellijId("refactoring.extractConstant") shouldBe RefactoringActionType.EXTRACT_CONSTANT
            }
            test("refactoring.encapsulateFields -> ENCAPSULATE_FIELD") {
                RefactoringActionType.fromIntellijId("refactoring.encapsulateFields") shouldBe RefactoringActionType.ENCAPSULATE_FIELD
            }
            test("refactoring.move -> MOVE_CLASS") {
                RefactoringActionType.fromIntellijId("refactoring.move") shouldBe RefactoringActionType.MOVE_CLASS
            }
        }

        context("keyword fallback") {
            test("ExtractFunction -> EXTRACT_METHOD") {
                RefactoringActionType.fromIntellijId("ExtractFunction") shouldBe RefactoringActionType.EXTRACT_METHOD
            }
            test("IntroduceFunction -> EXTRACT_METHOD") {
                RefactoringActionType.fromIntellijId("IntroduceFunction") shouldBe RefactoringActionType.EXTRACT_METHOD
            }
            test("IntroduceVariable -> EXTRACT_VARIABLE") {
                RefactoringActionType.fromIntellijId("IntroduceVariable") shouldBe RefactoringActionType.EXTRACT_VARIABLE
            }
            test("IntroduceConstant -> EXTRACT_CONSTANT") {
                RefactoringActionType.fromIntellijId("IntroduceConstant") shouldBe RefactoringActionType.EXTRACT_CONSTANT
            }
            test("InlineFunction -> INLINE_METHOD") {
                RefactoringActionType.fromIntellijId("InlineFunction") shouldBe RefactoringActionType.INLINE_METHOD
            }
            test("InlineVariable -> INLINE_VARIABLE") {
                RefactoringActionType.fromIntellijId("InlineVariable") shouldBe RefactoringActionType.INLINE_VARIABLE
            }
            test("RenameElement -> RENAME") {
                RefactoringActionType.fromIntellijId("RenameElement") shouldBe RefactoringActionType.RENAME
            }
            test("ChangeSignature -> CHANGE_SIGNATURE") {
                RefactoringActionType.fromIntellijId("ChangeSignature") shouldBe RefactoringActionType.CHANGE_SIGNATURE
            }
            test("SafeDelete -> SAFE_DELETE") {
                RefactoringActionType.fromIntellijId("SafeDelete") shouldBe RefactoringActionType.SAFE_DELETE
            }
            test("Move -> MOVE_METHOD") {
                RefactoringActionType.fromIntellijId("Move") shouldBe RefactoringActionType.MOVE_METHOD
            }
            test("EncapsulateField -> ENCAPSULATE_FIELD") {
                RefactoringActionType.fromIntellijId("EncapsulateField") shouldBe RefactoringActionType.ENCAPSULATE_FIELD
            }
            test("unknown -> null") {
                RefactoringActionType.fromIntellijId("unknown.refactoring") shouldBe null
            }
            test("should be case insensitive") {
                RefactoringActionType.fromIntellijId("RENAME") shouldBe RefactoringActionType.RENAME
                RefactoringActionType.fromIntellijId("rename") shouldBe RefactoringActionType.RENAME
                RefactoringActionType.fromIntellijId("ReName") shouldBe RefactoringActionType.RENAME
            }
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
            logicActions.size shouldBe 1
            logicActions shouldContain RefactoringActionType.REMOVE_DEAD_CODE
        }

        test("should have actions in DATA category") {
            val dataActions = RefactoringActionType.entries.filter { it.category == ActionCategory.DATA }
            dataActions.size shouldBe 1
            dataActions shouldContain RefactoringActionType.ENCAPSULATE_FIELD
        }
    }

    context("XP rewards") {
        test("simple refactorings should give low XP") {
            RefactoringActionType.RENAME.baseXP shouldBe 5
            RefactoringActionType.EXTRACT_VARIABLE.baseXP shouldBe 5
        }

        test("complex refactorings should give high XP") {
            RefactoringActionType.EXTRACT_METHOD.baseXP shouldBeGreaterThan RefactoringActionType.RENAME.baseXP
            RefactoringActionType.MOVE_METHOD.baseXP shouldBeGreaterThan RefactoringActionType.RENAME.baseXP
        }

        test("all XP values should be positive and reasonable") {
            RefactoringActionType.entries.forEach { action ->
                action.baseXP shouldBeGreaterThan 0
                action.baseXP shouldBe action.baseXP // sanity check
            }
        }
    }
})
