
# 🧩 1. Familles de refactorings (base du gameplay)

JetBrains expose (ou permet d’implémenter) presque tous ceux de Fowler.

## 🧱 A — Structure du code

| Action                     | Ce que ça améliore   | Gameplay             |
| -------------------------- | -------------------- | -------------------- |
| Extract Method             | lisibilité           | 🧪 +XP Clarity       |
| Inline Method              | over-abstraction     | 🗡️ anti-boilerplate |
| Extract Class              | classes trop grosses | 🏗️ Architecture     |
| Move Method                | mauvais couplage     | 🔀 Balance           |
| Rename                     | intention            | ✨ Clarity            |
| Change Signature           | API propre           | 🔧 Design            |
| Introduce Parameter Object | trop de paramètres   | 🧳 Packing           |
| Remove Parameter           | API minimaliste      | ✂️ Simplicity        |

---

## 🧠 B — Logique & complexité

| Action                                | Ce que ça corrige | Smell    |
| ------------------------------------- | ----------------- | -------- |
| Replace Conditional with Polymorphism | if/else géants    | 🐍 Hydra |
| Decompose Conditional                 | lisibilité        | 🧩       |
| Consolidate Conditionals              | duplication       | 👯       |
| Remove Dead Code                      | branches mortes   | 🧟       |
| Simplify Boolean                      | logique sale      | 🧠       |

---

## 📦 C — Données & état

| Action                         | Smell             |
| ------------------------------ | ----------------- |
| Encapsulate Field              | données publiques |
| Replace Data Class with Object | objets passifs    |
| Remove Setting Method          | mutabilité        |
| Introduce Value Object         | types primitifs   |

---

## 🔗 D — Couplage

| Action                              | Smell             |
| ----------------------------------- | ----------------- |
| Introduce Interface                 | dépendances dures |
| Dependency Inversion                | rigidité          |
| Replace Inheritance with Delegation | hiérarchie cassée |
| Break Cyclic Dependency             | cycles            |

---

# 👹 2. Catalogue de Code Smells → Ennemis

Tu peux mapper tout ça.

## Niveau fichier

| Smell           | Monstre    |
| --------------- | ---------- |
| >500 LOC        | 🗿 Giant   |
| >10 methods     | 🐙 Octopus |
| God Class       | 👑 Tyrant  |
| Too many fields | 🧳 Hoarder |
| High fan-in     | 🕷️ Web    |

---

## Niveau méthode

| Smell           | Monstre   |
| --------------- | --------- |
| >20 LOC         | 🐉 Dragon |
| >5 params       | 🧟 Mutant |
| Cyclomatic > 10 | 🐍 Hydra  |
| Nested ifs      | 🕸️ Trap  |
| Long lambda     | 👻 Ghost  |

---

## Niveau projet

| Smell               | Boss           |
| ------------------- | -------------- |
| Test coverage < 50% | 🧪 Necromancer |
| >100 TODO           | 🗒️ Chaos      |
| Cyclic deps         | 🌀 Ouroboros   |
| Duplication >10%    | 👯 Doppelking  |

---

# 🛠 3. Actions IDE que tu peux scorer

JetBrains peut détecter :

### Refactorings

* Rename
* Extract
* Inline
* Move
* Pull up / Push down
* Change signature
* Introduce field
* Introduce variable
* Introduce constant
* Convert to record / data class

### Nettoyage

* Optimize imports
* Reformat
* Remove unused
* Convert loops to streams
* Simplify expression

### Analyse

* Run inspections
* Run static analysis
* Apply quick-fix
* Apply intention

### Tests

* Run tests
* Add tests
* Increase coverage
* Fix failing tests

### Git

* Commit
* Squash
* Rebase
* Resolve conflicts

---

# 🎮 4. Combos de refactoring (vrai game design)

Tu peux créer des patterns experts :

| Combo                                       | Sens                    |
| ------------------------------------------- | ----------------------- |
| Rename → Extract → Move                     | nettoyage architectural |
| Extract → Introduce Interface → Move        | découplage              |
| Inline → Simplify → Rename                  | désencombrement         |
| Remove param → Change signature → Fix tests | API design              |

Chaque combo = multiplicateur d’XP.

---

# 🧠 5. Quêtes générées automatiquement

Tu peux générer des quêtes depuis le PSI.

Ex :

> “This class has 3 long methods → extract 2 of them”

Ou :

> “This package has 2 cyclic dependencies → break one”

Ou :

> “This method has 6 params → introduce parameter object”

---

# 🧬 6. Skill Tree très profond

Tu peux faire de vraies spécialisations :

```
                🧙 Refactoring Mage
               /        |         \
      🧪 Test Alchemist 🧱 Architect 🧠 Clean Coder
         |                |               |
  Coverage bonuses   Class bonuses   Naming bonuses
```

---

# 💎 7. Ce que tu es en train de créer

Tu n’es pas en train de faire un gadget.

Tu es en train de créer :

> **Un système de feedback comportemental pour développeurs.**

Les IDE n’enseignent pas → ils réagissent.
Ton plugin va **récompenser le bon comportement en temps réel**.

C’est exactement ce qui manque aujourd’hui