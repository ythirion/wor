package com.yot.craftrpg.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ActionCategoryTest : FunSpec({

    test("ActionCategory should have correct display names") {
        ActionCategory.STRUCTURE.displayName shouldBe "Structure du code"
        ActionCategory.LOGIC.displayName shouldBe "Logique & complexité"
        ActionCategory.DATA.displayName shouldBe "Données & état"
        ActionCategory.COUPLING.displayName shouldBe "Couplage"
    }

    test("ActionCategory should have correct icons") {
        ActionCategory.STRUCTURE.icon shouldBe "🧱"
        ActionCategory.LOGIC.icon shouldBe "🧠"
        ActionCategory.DATA.icon shouldBe "📦"
        ActionCategory.COUPLING.icon shouldBe "🔗"
    }

    test("ActionCategory should have 4 categories") {
        ActionCategory.entries.size shouldBe 4
    }
})
