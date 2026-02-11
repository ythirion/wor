# WoR - World Of Refactoring

## [Unreleased]

### Added

### Changed

### Fixed

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

[Unreleased]: https://github.com/ythirion/wor/compare/0.0.3...HEAD
[0.0.3]: https://github.com/ythirion/wor/compare/0.0.1...0.0.3
[0.0.1]: https://github.com/ythirion/wor/commits/0.0.1
