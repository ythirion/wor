package com.yot.wor.domain

enum class ActionCategory(val displayName: String, val icon: String) {
    STRUCTURE("Code Structure", "🧱"),
    LOGIC("Logic & Complexity", "🧠"),
    DATA("Data & State", "📦"),
    COUPLING("Coupling", "🔗")
}