package com.yot.craftrpg.domain

import com.intellij.psi.PsiElement

/**
 * Représente un "monstre" (code smell) détecté dans le code
 */
data class CodeMonster(
    val type: MonsterType,
    val location: MonsterLocation,
    val severity: Int, // Score de gravité (utilisé pour l'XP du combat)
    val description: String,
    val element: PsiElement? = null
)

/**
 * Type de monstre basé sur le backlog
 */
enum class MonsterType(
    val displayName: String,
    val icon: String,
    val level: MonsterLevel,
    val baseXPReward: Int
) {
    // Niveau Méthode
    DRAGON("Dragon", "🐉", MonsterLevel.METHOD, 50),           // >20 LOC
    MUTANT("Mutant", "🧟", MonsterLevel.METHOD, 30),           // >5 params
    HYDRA("Hydra", "🐍", MonsterLevel.METHOD, 60),            // Cyclomatic > 10
    TRAP("Trap", "🕸️", MonsterLevel.METHOD, 40),              // Nested ifs
    GHOST("Ghost", "👻", MonsterLevel.METHOD, 35),            // Long lambda

    // Niveau Fichier
    GIANT("Giant", "🗿", MonsterLevel.FILE, 100),             // >500 LOC
    OCTOPUS("Octopus", "🐙", MonsterLevel.FILE, 80),          // >10 methods
    TYRANT("Tyrant", "👑", MonsterLevel.FILE, 150),           // God Class
    HOARDER("Hoarder", "🧳", MonsterLevel.FILE, 70),          // Too many fields
    WEB("Web", "🕷️", MonsterLevel.FILE, 90),                 // High fan-in

    // Niveau Projet
    NECROMANCER("Necromancer", "🧪", MonsterLevel.PROJECT, 200), // Coverage < 50%
    CHAOS("Chaos", "🗒️", MonsterLevel.PROJECT, 150),            // >100 TODOs
    OUROBOROS("Ouroboros", "🌀", MonsterLevel.PROJECT, 250),     // Cyclic deps
    DOPPELKING("Doppelking", "👯", MonsterLevel.PROJECT, 180);   // Duplication >10%

    /**
     * Calcule l'XP de récompense basé sur la sévérité
     */
    fun calculateXPReward(severity: Int): Int {
        return baseXPReward + (severity * 10)
    }
}

/**
 * Niveau de détection du monstre
 */
enum class MonsterLevel {
    METHOD,
    FILE,
    PROJECT
}

/**
 * Localisation du monstre dans le code
 */
sealed class MonsterLocation {
    data class Method(
        val fileName: String,
        val className: String?,
        val methodName: String,
        val lineNumber: Int
    ) : MonsterLocation()

    data class File(
        val fileName: String,
        val filePath: String
    ) : MonsterLocation()

    data class Project(
        val description: String
    ) : MonsterLocation()
}

/**
 * Statistiques de monstres détectés
 */
data class MonsterStats(
    val totalMonsters: Int = 0,
    val monstersByType: Map<MonsterType, Int> = emptyMap(),
    val monstersByLevel: Map<MonsterLevel, Int> = emptyMap(),
    val totalSeverity: Int = 0,
    val slayedMonsters: Int = 0 // Monstres "tués" (code smells corrigés)
) {
    val averageSeverity: Double
        get() = if (totalMonsters > 0) totalSeverity.toDouble() / totalMonsters else 0.0
}
