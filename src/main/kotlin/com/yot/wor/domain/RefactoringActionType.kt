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
    EXTRACT_CLASS("Extract Class", ActionCategory.STRUCTURE, 15, "🏗️ Architecture"),
    MOVE_METHOD("Move Method", ActionCategory.STRUCTURE, 12, "🔀 Balance"),
    RENAME("Rename", ActionCategory.STRUCTURE, 5, "✨ Clarity"),
    CHANGE_SIGNATURE("Change Signature", ActionCategory.STRUCTURE, 10, "🔧 Design"),
    INTRODUCE_PARAMETER_OBJECT("Introduce Parameter Object", ActionCategory.STRUCTURE, 15, "🧳 Packing"),
    REMOVE_PARAMETER("Remove Parameter", ActionCategory.STRUCTURE, 8, "✂️ Simplicity"),
    EXTRACT_VARIABLE("Extract Variable", ActionCategory.STRUCTURE, 5, "🧪 Clarity"),
    EXTRACT_CONSTANT("Extract Constant", ActionCategory.STRUCTURE, 5, "🧪 Clarity"),
    EXTRACT_FIELD("Extract Field", ActionCategory.STRUCTURE, 8, "🧪 Clarity"),
    PULL_UP("Pull Up", ActionCategory.STRUCTURE, 12, "🏗️ Architecture"),
    PUSH_DOWN("Push Down", ActionCategory.STRUCTURE, 12, "🏗️ Architecture"),

    // B — Logique & complexité
    REPLACE_CONDITIONAL_WITH_POLYMORPHISM(
        "Replace Conditional with Polymorphism",
        ActionCategory.LOGIC,
        20,
        "🐍 Hydra Slayer"
    ),
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
    REPLACE_INHERITANCE_WITH_DELEGATION(
        "Replace Inheritance with Delegation",
        ActionCategory.COUPLING,
        18,
        "🔗 Delegator"
    ),
    BREAK_CYCLIC_DEPENDENCY("Break Cyclic Dependency", ActionCategory.COUPLING, 25, "🌀 Cycle Breaker"),

    // Nettoyage (Section 3)
    OPTIMIZE_IMPORTS("Optimize Imports", ActionCategory.STRUCTURE, 2, "🧹 Cleaner"),
    REFORMAT_CODE("Reformat Code", ActionCategory.STRUCTURE, 3, "🎨 Formatter"),
    REMOVE_UNUSED("Remove Unused", ActionCategory.STRUCTURE, 5, "🗑️ Janitor"),
    CONVERT_TO_STREAM("Convert to Stream", ActionCategory.LOGIC, 10, "🌊 Modernizer"),
    SIMPLIFY_EXPRESSION("Simplify Expression", ActionCategory.LOGIC, 8, "🧠 Simplifier"),

    SAFE_DELETE("Safe Delete", ActionCategory.STRUCTURE, 5, "🗑️ Safe Remover"),
    MOVE_CLASS("Move Class", ActionCategory.STRUCTURE, 12, "🔀 Organizer");

    companion object {
        fun fromIntellijId(id: String): RefactoringActionType? {
            val normalizedId = id.lowercase().replace(".", "_").replace("-", "_")

            return when {
                // Extract operations
                normalizedId in listOf("extractfunction", "extractmethod", "introducefunction", "introducemethod") -> EXTRACT_METHOD
                normalizedId.contains("extract") && (normalizedId.contains("method") || normalizedId.contains("function")) -> EXTRACT_METHOD
                normalizedId.contains("extract") && normalizedId.contains("class") -> EXTRACT_CLASS
                normalizedId == "introducevariable" || (normalizedId.contains("extract") && normalizedId.contains("variable")) -> EXTRACT_VARIABLE
                normalizedId == "introduceconstant" || (normalizedId.contains("extract") && normalizedId.contains("constant")) -> EXTRACT_CONSTANT
                normalizedId in listOf("introduceproperty", "introducefield") || (normalizedId.contains("extract") && (normalizedId.contains("field") || normalizedId.contains("property"))) -> EXTRACT_FIELD

                // Inline operations
                normalizedId == "inlinevariable" || (normalizedId.contains("inline") && normalizedId.contains("variable")) -> INLINE_VARIABLE
                normalizedId == "inlinefunction" || (normalizedId.contains("inline") && (normalizedId.contains("method") || normalizedId.contains("function"))) -> INLINE_METHOD

                // Move operations
                normalizedId == "move" || (normalizedId.contains("move") && (normalizedId.contains("method") || normalizedId.contains("function"))) -> MOVE_METHOD
                normalizedId.contains("move") && normalizedId.contains("class") -> MOVE_CLASS

                // Rename & signature
                normalizedId in listOf("renameelement", "rename") || normalizedId.contains("rename") -> RENAME
                normalizedId == "changesignature" || normalizedId.contains("change_signature") -> CHANGE_SIGNATURE

                // Parameter operations
                normalizedId.contains("introduce") && normalizedId.contains("parameter") && normalizedId.contains("object") -> INTRODUCE_PARAMETER_OBJECT
                normalizedId.contains("remove") && normalizedId.contains("parameter") -> REMOVE_PARAMETER

                // Pull/Push operations
                normalizedId.contains("pullup") || normalizedId.contains("pull_up") -> PULL_UP
                normalizedId.contains("pushdown") || normalizedId.contains("push_down") -> PUSH_DOWN

                // Conditional & logic operations
                normalizedId.contains("replace") && normalizedId.contains("conditional") && normalizedId.contains("polymorphism") -> REPLACE_CONDITIONAL_WITH_POLYMORPHISM
                normalizedId.contains("decompose") && normalizedId.contains("conditional") -> DECOMPOSE_CONDITIONAL
                normalizedId.contains("consolidate") && normalizedId.contains("conditional") -> CONSOLIDATE_CONDITIONALS
                normalizedId.contains("simplify") && normalizedId.contains("boolean") -> SIMPLIFY_BOOLEAN
                normalizedId.contains("simplify") && normalizedId.contains("expression") -> SIMPLIFY_EXPRESSION

                // Code cleanup
                normalizedId == "safedelete" || normalizedId.contains("safe_delete") -> SAFE_DELETE
                (normalizedId.contains("remove") && normalizedId.contains("dead")) || normalizedId.contains("deadcode") -> REMOVE_DEAD_CODE
                normalizedId.contains("remove") && normalizedId.contains("unused") -> REMOVE_UNUSED

                // Data operations
                normalizedId.contains("encapsulate") && (normalizedId.contains("field") || normalizedId.contains("property")) -> ENCAPSULATE_FIELD
                normalizedId.contains("replace") && normalizedId.contains("data") && normalizedId.contains("class") -> REPLACE_DATA_CLASS_WITH_OBJECT
                normalizedId.contains("remove") && normalizedId.contains("setting") && (normalizedId.contains("method") || normalizedId.contains("property")) -> REMOVE_SETTING_METHOD
                normalizedId.contains("introduce") && normalizedId.contains("value") && normalizedId.contains("object") -> INTRODUCE_VALUE_OBJECT

                // Interface & coupling operations
                normalizedId == "extractinterface" || ((normalizedId.contains("extract") || normalizedId.contains("introduce")) && normalizedId.contains("interface")) -> INTRODUCE_INTERFACE
                normalizedId.contains("dependency") && normalizedId.contains("inversion") -> DEPENDENCY_INVERSION
                normalizedId.contains("replace") && normalizedId.contains("inheritance") && normalizedId.contains("delegation") -> REPLACE_INHERITANCE_WITH_DELEGATION
                (normalizedId.contains("break") && normalizedId.contains("cyclic")) || (normalizedId.contains("break") && normalizedId.contains("cycle")) -> BREAK_CYCLIC_DEPENDENCY

                // Modernization
                normalizedId.contains("convert") && normalizedId.contains("stream") -> CONVERT_TO_STREAM

                // Formatting & imports
                normalizedId.contains("optimize") && normalizedId.contains("import") -> OPTIMIZE_IMPORTS
                normalizedId.contains("reformat") && normalizedId.contains("code") -> REFORMAT_CODE

                else -> null
            }
        }
    }
}
