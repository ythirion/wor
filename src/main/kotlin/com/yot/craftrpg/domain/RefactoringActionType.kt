package com.yot.craftrpg.domain

/**
 * Types d'actions de refactoring détectables dans l'IDE
 * Basé sur le backlog - Section 1 & 3
 */
enum class RefactoringActionType(
    val displayName: String,
    val category: ActionCategory,
    val baseXP: Int,
    val gameplayTag: String
) {
    // A — Structure du code
    EXTRACT_METHOD("Extract Method", ActionCategory.STRUCTURE, 10, "🧪 Clarity"),
    INLINE_METHOD("Inline Method", ActionCategory.STRUCTURE, 8, "🗡️ Anti-boilerplate"),
    EXTRACT_CLASS("Extract Class", ActionCategory.STRUCTURE, 15, "🏗️ Architecture"),
    MOVE_METHOD("Move Method", ActionCategory.STRUCTURE, 12, "🔀 Balance"),
    RENAME("Rename", ActionCategory.STRUCTURE, 5, "✨ Clarity"),
    CHANGE_SIGNATURE("Change Signature", ActionCategory.STRUCTURE, 10, "🔧 Design"),
    INTRODUCE_PARAMETER_OBJECT("Introduce Parameter Object", ActionCategory.STRUCTURE, 15, "🧳 Packing"),
    REMOVE_PARAMETER("Remove Parameter", ActionCategory.STRUCTURE, 8, "✂️ Simplicity"),

    // A — Structure (autres)
    EXTRACT_VARIABLE("Extract Variable", ActionCategory.STRUCTURE, 5, "🧪 Clarity"),
    EXTRACT_CONSTANT("Extract Constant", ActionCategory.STRUCTURE, 5, "🧪 Clarity"),
    EXTRACT_FIELD("Extract Field", ActionCategory.STRUCTURE, 8, "🧪 Clarity"),
    PULL_UP("Pull Up", ActionCategory.STRUCTURE, 12, "🏗️ Architecture"),
    PUSH_DOWN("Push Down", ActionCategory.STRUCTURE, 12, "🏗️ Architecture"),

    // B — Logique & complexité
    REPLACE_CONDITIONAL_WITH_POLYMORPHISM("Replace Conditional with Polymorphism", ActionCategory.LOGIC, 20, "🐍 Hydra Slayer"),
    DECOMPOSE_CONDITIONAL("Decompose Conditional", ActionCategory.LOGIC, 12, "🧩 Clarity"),
    CONSOLIDATE_CONDITIONALS("Consolidate Conditionals", ActionCategory.LOGIC, 10, "👯 Deduplicator"),
    REMOVE_DEAD_CODE("Remove Dead Code", ActionCategory.LOGIC, 8, "🧟 Zombie Hunter"),
    SIMPLIFY_BOOLEAN("Simplify Boolean", ActionCategory.LOGIC, 8, "🧠 Logic Master"),

    // C — Données & état
    ENCAPSULATE_FIELD("Encapsulate Field", ActionCategory.DATA, 10, "🔒 Protector"),
    REPLACE_DATA_CLASS_WITH_OBJECT("Replace Data Class with Object", ActionCategory.DATA, 15, "📦 Enricher"),
    REMOVE_SETTING_METHOD("Remove Setting Method", ActionCategory.DATA, 10, "🔐 Immutability"),
    INTRODUCE_VALUE_OBJECT("Introduce Value Object", ActionCategory.DATA, 15, "💎 Value Creator"),

    // D — Couplage
    INTRODUCE_INTERFACE("Introduce Interface", ActionCategory.COUPLING, 15, "🔗 Decoupler"),
    DEPENDENCY_INVERSION("Dependency Inversion", ActionCategory.COUPLING, 20, "🔗 Inverter"),
    REPLACE_INHERITANCE_WITH_DELEGATION("Replace Inheritance with Delegation", ActionCategory.COUPLING, 18, "🔗 Delegator"),
    BREAK_CYCLIC_DEPENDENCY("Break Cyclic Dependency", ActionCategory.COUPLING, 25, "🌀 Cycle Breaker"),

    // Nettoyage (Section 3)
    OPTIMIZE_IMPORTS("Optimize Imports", ActionCategory.STRUCTURE, 2, "🧹 Cleaner"),
    REFORMAT_CODE("Reformat Code", ActionCategory.STRUCTURE, 3, "🎨 Formatter"),
    REMOVE_UNUSED("Remove Unused", ActionCategory.STRUCTURE, 5, "🗑️ Janitor"),
    CONVERT_TO_STREAM("Convert to Stream", ActionCategory.LOGIC, 10, "🌊 Modernizer"),
    SIMPLIFY_EXPRESSION("Simplify Expression", ActionCategory.LOGIC, 8, "🧠 Simplifier"),

    // Autres actions utiles
    SAFE_DELETE("Safe Delete", ActionCategory.STRUCTURE, 5, "🗑️ Safe Remover"),
    MOVE_CLASS("Move Class", ActionCategory.STRUCTURE, 12, "🔀 Organizer");

    companion object {
        /**
         * Trouve le type d'action correspondant à un ID IntelliJ
         */
        fun fromIntellijId(id: String): RefactoringActionType? {
            return when {
                id.contains("extract", ignoreCase = true) && id.contains("method", ignoreCase = true) -> EXTRACT_METHOD
                id.contains("inline", ignoreCase = true) && id.contains("method", ignoreCase = true) -> INLINE_METHOD
                id.contains("extract", ignoreCase = true) && id.contains("class", ignoreCase = true) -> EXTRACT_CLASS
                id.contains("move", ignoreCase = true) && id.contains("method", ignoreCase = true) -> MOVE_METHOD
                id.contains("move", ignoreCase = true) && id.contains("class", ignoreCase = true) -> MOVE_CLASS
                id.contains("rename", ignoreCase = true) -> RENAME
                id.contains("changeSignature", ignoreCase = true) -> CHANGE_SIGNATURE
                id.contains("extract", ignoreCase = true) && id.contains("variable", ignoreCase = true) -> EXTRACT_VARIABLE
                id.contains("extract", ignoreCase = true) && id.contains("constant", ignoreCase = true) -> EXTRACT_CONSTANT
                id.contains("extract", ignoreCase = true) && id.contains("field", ignoreCase = true) -> EXTRACT_FIELD
                id.contains("pullUp", ignoreCase = true) -> PULL_UP
                id.contains("pushDown", ignoreCase = true) -> PUSH_DOWN
                id.contains("encapsulate", ignoreCase = true) -> ENCAPSULATE_FIELD
                id.contains("optimizeImports", ignoreCase = true) -> OPTIMIZE_IMPORTS
                id.contains("reformatCode", ignoreCase = true) -> REFORMAT_CODE
                id.contains("safeDelete", ignoreCase = true) -> SAFE_DELETE
                else -> null
            }
        }
    }
}
