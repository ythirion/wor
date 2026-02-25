# WoR - World Of Refactoring

## [Unreleased]

## [0.1.0] - 2026-02-25

### Changed

- Use exact ids for IntelliJ / WebStorm
- Improve fallback actions identification

## [0.0.7] - 2026-02-16

### Added

- Use AnActionListener to support non-natively supported languages (kotlin for example)

## [0.0.6] - 2026-02-16

### Changed

- Improve refactoring actions mapping

## [0.0.5] - 2026-02-12

### Changed

- Add panel to list completed quests

### Fixed

- Finish quests for real

## [0.0.4] - 2026-02-11

## [0.0.3] - 2026-02-11

### Added

- Global state persistence across all projects - your XP and level are now shared between all IntelliJ projects
- Quest progress now saved automatically and persists across IDE restarts
- Better visibility of the Quests
- Add icons for each refactorer levels

### Changed

- PlayerStateService and QuestService now run at Application level instead of Project level
- State is stored globally in IDE configuration rather than per-project

## [0.0.1] - 2026-02-09

### Added

- Initial scaffold created
  from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

[Unreleased]: https://github.com/ythirion/wor/compare/0.1.0...HEAD
[0.1.0]: https://github.com/ythirion/wor/compare/0.0.7...0.1.0
[0.0.7]: https://github.com/ythirion/wor/compare/0.0.6...0.0.7
[0.0.6]: https://github.com/ythirion/wor/compare/0.0.5...0.0.6
[0.0.5]: https://github.com/ythirion/wor/compare/0.0.4...0.0.5
[0.0.4]: https://github.com/ythirion/wor/compare/0.0.3...0.0.4
[0.0.3]: https://github.com/ythirion/wor/compare/0.0.1...0.0.3
[0.0.1]: https://github.com/ythirion/wor/commits/0.0.1
