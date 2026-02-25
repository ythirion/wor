package com.yot.wor.domain

enum class RefactoringActionType(
    val displayName: String,
    val category: ActionCategory,
    val baseXP: Int,
    val gameplayTag: String
) {
    // A — Structure du code
    EXTRACT_METHOD("Extract Method", ActionCategory.STRUCTURE, 10, "🧪 Clarity"),
    INLINE_METHOD("Inline Method", ActionCategory.STRUCTURE, 8, "🗡️ Anti-boilerplate"),
    INLINE_VARIABLE("Inline Variable", ActionCategory.STRUCTURE, 5, "🗡️ Anti-boilerplate"),
    MOVE_METHOD("Move Method", ActionCategory.STRUCTURE, 12, "🔀 Balance"),
    RENAME("Rename", ActionCategory.STRUCTURE, 5, "✨ Clarity"),
    CHANGE_SIGNATURE("Change Signature", ActionCategory.STRUCTURE, 10, "🔧 Design"),
    REMOVE_PARAMETER("Remove Parameter", ActionCategory.STRUCTURE, 8, "✂️ Simplicity"),
    EXTRACT_VARIABLE("Extract Variable", ActionCategory.STRUCTURE, 5, "🧪 Clarity"),
    EXTRACT_CONSTANT("Extract Constant", ActionCategory.STRUCTURE, 5, "🧪 Clarity"),
    SAFE_DELETE("Safe Delete", ActionCategory.STRUCTURE, 5, "🗑️ Safe Remover"),
    MOVE_CLASS("Move Class", ActionCategory.STRUCTURE, 12, "🔀 Organizer"),

    // B — Logique & complexité
    REMOVE_DEAD_CODE("Remove Dead Code", ActionCategory.LOGIC, 8, "🧟 Zombie Hunter"),

    // C — Données & état
    ENCAPSULATE_FIELD("Encapsulate Field", ActionCategory.DATA, 10, "🔒 Protector");

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
