package com.yot.craftrpg.domain

/**
 * Les 4 familles de refactorings du backlog
 */
enum class ActionCategory(val displayName: String, val icon: String) {
    STRUCTURE("Structure du code", "🧱"),
    LOGIC("Logique & complexité", "🧠"),
    DATA("Données & état", "📦"),
    COUPLING("Couplage", "🔗")
}
