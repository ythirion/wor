package com.yot.craftrpg.domain

/**
 * The 4 refactoring families from the backlog
 */
enum class ActionCategory(val displayName: String, val icon: String) {
    STRUCTURE("Code Structure", "🧱"),
    LOGIC("Logic & Complexity", "🧠"),
    DATA("Data & State", "📦"),
    COUPLING("Coupling", "🔗")
}
