package com.yot.wor.icons

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class LevelIconsTest : FunSpec({

    context("Emoji fallbacks") {
        test("should return correct emoji for each level tier") {
            LevelIcons.getEmojiFallback(1) shouldBe "🌱"
            LevelIcons.getEmojiFallback(5) shouldBe "⚔️"
            LevelIcons.getEmojiFallback(15) shouldBe "🛡️"
            LevelIcons.getEmojiFallback(25) shouldBe "🎖️"
            LevelIcons.getEmojiFallback(40) shouldBe "👑"
            LevelIcons.getEmojiFallback(100) shouldBe "🧙"
        }
    }

    context("Level tier names") {
        test("should return correct tier name for each level") {
            LevelIcons.getLevelTierName(1) shouldBe "Apprentice"
            LevelIcons.getLevelTierName(5) shouldBe "Refactorer"
            LevelIcons.getLevelTierName(15) shouldBe "Expert"
            LevelIcons.getLevelTierName(25) shouldBe "Master"
            LevelIcons.getLevelTierName(40) shouldBe "Grand Master"
            LevelIcons.getLevelTierName(100) shouldBe "Legend"
        }
    }

    context("Icon paths") {
        test("should return correct icon path for each level") {
            LevelIcons.getIconPathForLevel(1) shouldContain "level_apprentice.png"
            LevelIcons.getIconPathForLevel(5) shouldContain "level_refactorer.png"
            LevelIcons.getIconPathForLevel(15) shouldContain "level_expert.png"
            LevelIcons.getIconPathForLevel(25) shouldContain "level_master.png"
            LevelIcons.getIconPathForLevel(40) shouldContain "level_grandmaster.png"
            LevelIcons.getIconPathForLevel(100) shouldContain "level_legend.png"
        }
    }

    context("Icon loading") {
        test("should handle missing icons gracefully") {
            LevelIcons.getIconForLevel(1) shouldNotBe null
        }
    }
})
