package com.yot.wor.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object LevelIcons {
    fun iconForLevel(level: Int): Icon? = try {
        IconLoader.findIcon(
            "/icons/levels/${
                when {
                    level < 5 -> "level_apprentice.png"      // 🌱 Apprentice
                    level < 10 -> "level_refactorer.png"     // ⚔️ Refactorer
                    level < 20 -> "level_expert.png"         // 🛡️ Expert
                    level < 30 -> "level_master.png"         // 🎖️ Master
                    level <= 50 -> "level_grandmaster.png"   // 👑 Grand Master
                    else -> "level_legend.png"                // 🧙 Legend
                }
            }", LevelIcons::class.java
        )
    } catch (_: Exception) {
        null
    }

    fun iconPathForLevel(level: Int): String = when {
        level < 5 -> "icons/levels/level_apprentice.png"
        level < 10 -> "icons/levels/level_refactorer.png"
        level < 20 -> "icons/levels/level_expert.png"
        level < 30 -> "icons/levels/level_master.png"
        level <= 50 -> "icons/levels/level_grandmaster.png"
        else -> "icons/levels/level_legend.png"
    }

    fun emojiFallback(level: Int): String = when {
        level < 5 -> "🌱"
        level < 10 -> "⚔️"
        level < 20 -> "🛡️"
        level < 30 -> "🎖️"
        level <= 50 -> "👑"
        else -> "🧙"
    }

    fun levelTierName(level: Int): String {
        return when {
            level < 5 -> "Apprentice"
            level < 10 -> "Refactorer"
            level < 20 -> "Expert"
            level < 30 -> "Master"
            level <= 50 -> "Grand Master"
            else -> "Legend"
        }
    }
}
