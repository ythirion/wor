package com.yot.wor.domain

enum class RefactoringActionType(
    val displayName: String,
    val category: ActionCategory,
    val baseXP: Int,
    val gameplayTag: String,
    val description: String,
    val howTo: String,
) {
    // A — Structure du code
    EXTRACT_METHOD(
        displayName = "Extract Method",
        category = ActionCategory.STRUCTURE,
        baseXP = 10,
        gameplayTag = "🧪 Clarity",
        description = "Transforms a selected code fragment into a new method with a meaningful name. Reduces duplication, improves readability, and makes large methods easier to understand.",
        howTo = "Select the code fragment → Refactor → Extract Method (⌘⌥M / Ctrl+Alt+M)",
    ),
    INLINE_METHOD(
        displayName = "Inline Method",
        category = ActionCategory.STRUCTURE,
        baseXP = 8,
        gameplayTag = "🗡️ Anti-boilerplate",
        description = "Replaces a method call with the method's body when the method is trivially simple, used only once, or adds no clarity. Eliminates unnecessary indirection.",
        howTo = "Place cursor on method name → Refactor → Inline Method (⌘⌥N / Ctrl+Alt+N)",
    ),
    INLINE_VARIABLE(
        displayName = "Inline Variable",
        category = ActionCategory.STRUCTURE,
        baseXP = 5,
        gameplayTag = "🗡️ Anti-boilerplate",
        description = "Replaces a variable with its initializer expression directly at each usage site, removing needless intermediate variables that don't add explanatory value.",
        howTo = "Place cursor on variable → Refactor → Inline Variable (⌘⌥N / Ctrl+Alt+N)",
    ),
    MOVE_METHOD(
        displayName = "Move Method",
        category = ActionCategory.STRUCTURE,
        baseXP = 12,
        gameplayTag = "🔀 Balance",
        description = "Moves a method to the class that uses it most or that owns the data it operates on. Improves cohesion and reduces feature envy between classes.",
        howTo = "Place cursor on method → Refactor → Move (F6)",
    ),
    RENAME(
        displayName = "Rename",
        category = ActionCategory.STRUCTURE,
        baseXP = 5,
        gameplayTag = "✨ Clarity",
        description = "Renames any identifier — variable, method, class, parameter — to better express its intent. All references across the codebase are updated automatically.",
        howTo = "Place cursor on element → Refactor → Rename (⇧F6)",
    ),
    CHANGE_SIGNATURE(
        displayName = "Change Signature",
        category = ActionCategory.STRUCTURE,
        baseXP = 10,
        gameplayTag = "🔧 Design",
        description = "Modifies a method's parameter list: add, remove, reorder, or rename parameters, and change their types. All call sites are updated automatically.",
        howTo = "Place cursor on method → Refactor → Change Signature (⌘F6 / Ctrl+F6)",
    ),
    REMOVE_PARAMETER(
        displayName = "Remove Parameter",
        category = ActionCategory.STRUCTURE,
        baseXP = 8,
        gameplayTag = "✂️ Simplicity",
        description = "Removes an unused or unnecessary parameter from a method signature. Simplifies the API and reduces cognitive load for callers.",
        howTo = "Place cursor on method → Refactor → Change Signature → select parameter → Delete (⌘F6 / Ctrl+F6)",
    ),
    EXTRACT_VARIABLE(
        displayName = "Extract Variable",
        category = ActionCategory.STRUCTURE,
        baseXP = 5,
        gameplayTag = "🧪 Clarity",
        description = "Introduces a named local variable for a complex or repeated expression. Makes the code self-documenting and avoids computing the same value multiple times.",
        howTo = "Select expression → Refactor → Extract Variable (⌘⌥V / Ctrl+Alt+V)",
    ),
    EXTRACT_CONSTANT(
        displayName = "Extract Constant",
        category = ActionCategory.STRUCTURE,
        baseXP = 5,
        gameplayTag = "🧪 Clarity",
        description = "Replaces a magic number or literal string with a named constant, centralizing its definition and giving meaning to otherwise opaque values.",
        howTo = "Select literal value → Refactor → Extract Constant (⌘⌥C / Ctrl+Alt+C)",
    ),
    SAFE_DELETE(
        displayName = "Safe Delete",
        category = ActionCategory.STRUCTURE,
        baseXP = 5,
        gameplayTag = "🗑️ Safe Remover",
        description = "Deletes a code element (class, method, field, parameter) only after verifying it is not referenced anywhere. Prevents accidental breakage when removing unused code.",
        howTo = "Select element → Refactor → Safe Delete (⌥⌦ / Alt+Delete)",
    ),
    MOVE_CLASS(
        displayName = "Move Class",
        category = ActionCategory.STRUCTURE,
        baseXP = 12,
        gameplayTag = "🔀 Organizer",
        description = "Moves a class to a more appropriate package or module, improving the overall organisation and cohesion of the codebase. All import statements are updated automatically.",
        howTo = "Right-click class → Refactor → Move Class (F6)",
    ),

    // B — Logique & complexité
    REMOVE_DEAD_CODE(
        displayName = "Remove Dead Code",
        category = ActionCategory.LOGIC,
        baseXP = 8,
        gameplayTag = "🧟 Zombie Hunter",
        description = "Identifies and deletes code that is never executed — unreachable branches, unused methods, obsolete conditions. Reduces noise and the risk of maintaining code that has no effect.",
        howTo = "Use IDE inspections (⌥⌘I / Ctrl+Alt+I) to find unused code warnings → Safe Delete",
    ),

    // C — Données & état
    ENCAPSULATE_FIELD(
        displayName = "Encapsulate Field",
        category = ActionCategory.DATA,
        baseXP = 10,
        gameplayTag = "🔒 Protector",
        description = "Wraps a public field with getter and setter methods, hiding the internal representation. Enables validation, change notification, and future implementation changes without breaking callers.",
        howTo = "Right-click field → Refactor → Encapsulate Fields",
    );

    companion object {
        /**
         * Exact WebStorm and IntelliJ IDEA action IDs.
         * Checked first before keyword fallback.
         * Collision notes:
         *   - refactoring.safeDelete → SAFE_DELETE (shared with REMOVE_PARAMETER & REMOVE_DEAD_CODE)
         *   - refactoring.javascript.es6.moveModule → MOVE_METHOD (shared with MOVE_CLASS in WebStorm)
         */
        val EXACT_IDS: Map<String, RefactoringActionType> = mapOf(
            // WebStorm
            "refactoring.javascript.extractMethod" to EXTRACT_METHOD,
            "refactoring.javascript.inline.method" to INLINE_METHOD,
            "refactoring.javascript.inline" to INLINE_VARIABLE,
            "refactoring.javascript.es6.moveModule" to MOVE_METHOD,
            "refactoring.inplace.rename" to RENAME,
            "refactoring.rename" to RENAME,
            "refactoring.javascript.change.signature" to CHANGE_SIGNATURE,
            "refactoring.safeDelete" to SAFE_DELETE,
            "refactoring.javascript.introduceVariable" to EXTRACT_VARIABLE,
            "refactoring.javascript.introduceConstant" to EXTRACT_CONSTANT,
            // IntelliJ IDEA
            "refactoring.extract.method" to EXTRACT_METHOD,
            "refactoring.inline.method" to INLINE_METHOD,
            "refactoring.inline.local.variable" to INLINE_VARIABLE,
            "refactoring.move.members" to MOVE_METHOD,
            "refactoring.changeSignature" to CHANGE_SIGNATURE,
            "refactoring.extractVariable" to EXTRACT_VARIABLE,
            "refactoring.extractConstant" to EXTRACT_CONSTANT,
            "refactoring.encapsulateFields" to ENCAPSULATE_FIELD,
            "refactoring.move" to MOVE_CLASS,
        )

        fun fromIntellijId(id: String): RefactoringActionType? {
            EXACT_IDS[id]?.let { return it }

            val lower = id.lowercase()
            return when {
                lower.contains("extractfunction") || lower.contains("introducefunction") || lower.contains("introducemethod")
                        || (lower.contains("extract") && (lower.contains("method") || lower.contains("function")))
                    -> EXTRACT_METHOD

                lower.contains("extractvariable") || lower.contains("introducevariable")
                        || (lower.contains("extract") && lower.contains("variable"))
                    -> EXTRACT_VARIABLE

                lower.contains("extractconstant") || lower.contains("introduceconstant")
                        || (lower.contains("extract") && lower.contains("constant"))
                    -> EXTRACT_CONSTANT

                lower.contains("inlinevariable") || (lower.contains("inline") && lower.contains("variable"))
                    -> INLINE_VARIABLE

                lower.contains("inlinefunction") || lower.contains("inlinemethod")
                        || (lower.contains("inline") && (lower.contains("method") || lower.contains("function")))
                    -> INLINE_METHOD

                lower.contains("move") && lower.contains("class") -> MOVE_CLASS
                lower == "move" || (lower.contains("move") && (lower.contains("method") || lower.contains("function")))
                    -> MOVE_METHOD

                lower.contains("renameelement") || lower.contains("rename") -> RENAME

                lower.contains("changesignature") || (lower.contains("change") && lower.contains("signature"))
                    -> CHANGE_SIGNATURE

                lower.contains("remove") && lower.contains("dead") -> REMOVE_DEAD_CODE
                lower.contains("remove") && lower.contains("param") -> REMOVE_PARAMETER
                lower.contains("safedelete") || lower.contains("safe_delete") -> SAFE_DELETE

                lower.contains("encapsulate") && (lower.contains("field") || lower.contains("property"))
                    -> ENCAPSULATE_FIELD

                else -> null
            }
        }
    }
}
