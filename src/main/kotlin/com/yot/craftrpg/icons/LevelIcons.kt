package com.yot.craftrpg.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Level icons loader
 */
object LevelIcons {

    /**
     * Gets the icon for a specific level
     * Icons should be placed in resources/icons/levels/
     * Named as: level_apprentice.png, level_refactorer.png, etc.
     */
    fun getIconForLevel(level: Int): Icon? {
        val iconName = when {
            level < 5 -> "level_apprentice.png"      // 🌱 Apprentice
            level < 10 -> "level_refactorer.png"     // ⚔️ Refactorer
            level < 20 -> "level_expert.png"         // 🛡️ Expert
            level < 30 -> "level_master.png"         // 🎖️ Master
            level <= 50 -> "level_grandmaster.png"   // 👑 Grand Master
            else -> "level_legend.png"                // 🧙 Legend
        }

        return try {
            IconLoader.getIcon("/icons/levels/$iconName", LevelIcons::class.java)
        } catch (e: Exception) {
            null // Return null if icon not found, UI will handle fallback
        }
    }

    /**
     * Gets the icon file path for export
     */
    fun getIconPathForLevel(level: Int): String {
        return when {
            level < 5 -> "icons/levels/level_apprentice.png"
            level < 10 -> "icons/levels/level_refactorer.png"
            level < 20 -> "icons/levels/level_expert.png"
            level < 30 -> "icons/levels/level_master.png"
            level <= 50 -> "icons/levels/level_grandmaster.png"
            else -> "icons/levels/level_legend.png"
        }
    }

    /**
     * Gets the emoji fallback for a level (used when PNG not available)
     */
    fun getEmojiFallback(level: Int): String {
        return when {
            level < 5 -> "🌱"
            level < 10 -> "⚔️"
            level < 20 -> "🛡️"
            level < 30 -> "🎖️"
            level <= 50 -> "👑"
            else -> "🧙"
        }
    }

    /**
     * Gets the level tier name
     */
    fun getLevelTierName(level: Int): String {
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
