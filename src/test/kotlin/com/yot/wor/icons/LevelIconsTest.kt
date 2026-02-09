package com.yot.wor.icons

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class LevelIconsTest : FunSpec({

    context("Emoji fallbacks") {
        test("should return correct emoji for each level tier") {
            LevelIcons.emojiFallback(1) shouldBe "🌱"
            LevelIcons.emojiFallback(5) shouldBe "⚔️"
            LevelIcons.emojiFallback(15) shouldBe "🛡️"
            LevelIcons.emojiFallback(25) shouldBe "🎖️"
            LevelIcons.emojiFallback(40) shouldBe "👑"
            LevelIcons.emojiFallback(100) shouldBe "🧙"
        }
    }

    context("Level tier names") {
        test("should return correct tier name for each level") {
            LevelIcons.levelTierName(1) shouldBe "Apprentice"
            LevelIcons.levelTierName(5) shouldBe "Refactorer"
            LevelIcons.levelTierName(15) shouldBe "Expert"
            LevelIcons.levelTierName(25) shouldBe "Master"
            LevelIcons.levelTierName(40) shouldBe "Grand Master"
            LevelIcons.levelTierName(100) shouldBe "Legend"
        }
    }

    context("Icon paths") {
        test("should return correct icon path for each level") {
            LevelIcons.iconPathForLevel(1) shouldContain "level_apprentice.png"
            LevelIcons.iconPathForLevel(5) shouldContain "level_refactorer.png"
            LevelIcons.iconPathForLevel(15) shouldContain "level_expert.png"
            LevelIcons.iconPathForLevel(25) shouldContain "level_master.png"
            LevelIcons.iconPathForLevel(40) shouldContain "level_grandmaster.png"
            LevelIcons.iconPathForLevel(100) shouldContain "level_legend.png"
        }
    }

    context("Icon loading") {
        test("should handle missing icons gracefully") {
            LevelIcons.iconForLevel(1) shouldNotBe null
        }
    }
})
