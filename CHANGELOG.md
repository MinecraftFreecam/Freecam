# Changelog

All notable changes to this project will be documented in this file.

This file is formatted as per [Keep a Changelog](https://keepachangelog.com/en/1.0.0),
and Freecam's versioning is based on [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- \[Fabric]: The name & description shown in the Mod Menu can now be translated ([#172](https://github.com/MinecraftFreecam/Freecam/issues/172)).

### Changed

### Removed

### Fixed

## [1.2.3] - 2024-02-04

### Added

- Neoforge support.
  - Replaces legacy Forge support.
- Translations can now be submitted using [Crowdin](https://crowdin.com/project/freecam).
- Japanese translations (Thanks, @Greenjp0025 !).
- This changelog document!

### Changed

- Improved "combo key" behavior.
  - The _toggle key_ no longer needs to be pressed at exactly the same time as the _tripod key_
    to toggle a tripod. Instead, it can be _held indefinitely_ before pressing a tripod key.
  - Freecam is now toggled when the toggle key is **released**.
  - Freecam is **not** toggled if the keypress was used to toggle a tripod.
- Various internal changes.

### Removed

- Forge support.
  - Removed in favor of Neoforge.

### Fixed

- "Ignore Transparent" collision not affecting glass blocks.
- Tripods can be set in non-vanilla dimensions.
- "Disable on Damage" preventing player movement ([#155](https://github.com/MinecraftFreecam/Freecam/issues/155)).

## [1.2.2] - 2023-12-25

### Platforms

- [Minecraft 1.20.4](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.2-mc1.20).

### Added

- Minecraft 1.20.4 support.

### Changed

- Forge version now uses Cloth Config.
- Lots of under-the-hood changes.

## [1.2.1.1] - 2023-10-05

### Platforms

- Minecraft 1.20: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1.1-mc1.20),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1.1-forge-mc1.20).

### Added

- Minecraft 1.20.2 support (Thank you @jmolloy19 !).

## [1.2.1] - 2023-07-13

### Platforms

- Minecraft 1.20: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-mc1.20),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-forge-mc1.20).
- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.1-forge-mc1.16).

### Changed

- Updated mod description.

### Fixed

- Fixed nametag displaying in inventory screen while freecam is enabled.
- Fixed bubble column sound playing in freecam.
- Fixed collision with solid entities (shulkers/boats).
- Fixed 'Show Player' setting not doing anything on Forge version.
- Fixed crash on Fabric 1.16 version.

## [1.2.0] - 2023-06-18

### Platforms

- Minecraft 1.20: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-mc1.20),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-forge-mc1.20).
- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.2.0-forge-mc1.16).

### Added

- Added 'Full Brightness' option.
- Added Config GUI keybind.

### Changed

- Reorganized config screen.
- Updated some setting descriptions.
- Updated translations.

### Fixed

- Fixed collision not being ignored for certain blocks.
- Third person, if previously enabled, is now restored upon exiting freecam.

## [1.1.10] - 2023-06-15

### Platforms

- Minecraft 1.20: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-mc1.20),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-forge-mc1.20).
- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.10-forge-mc1.16).

### Added

- Minecraft 1.20 support.
- Added new 'Ignore Transparent Blocks' and 'Ignore Openable Blocks' collision options.

### Changed

- Replaced 'No Clip' setting with 'Ignore All Collision' setting.

### Fixed

- Fixed incorrect translation key.

## [1.1.9] - 2023-03-19

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.9-forge-mc1.16).

### Added

- Minecraft 1.19.4 support.
- Added 'Show Submersion Fog' option.
- Added 'Initial Perspective' option.

### Fixed

- Fixed 'Disable on Damage' false positives.
- Fixed water submersion sounds playing while in Freecam.

## [1.1.8] - 2022-12-19

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.8-forge-mc1.16).

### Added

- Added description to mod list screen.

### Changed

- Improved tripod performance.
- Made more strings translatable.
- Updated Italian and Chinese translations (credits to @Loweredgames and @GodGun968).
- Updated license to MIT in mod list screen.
- Tripods are now stored separately per-dimension.

### Fixed

- Fixed crash when enabling freecam whilst on fire or in lava with 'Disable on Damage' set to true.
- Disable on damage now ignores damage received in creative mode.
- Tripods are no-longer cleared when switching dimensions.
- Fixed being able to hit tripods.

## [1.1.7] - 2022-12-10

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.7-forge-mc1.16).

### Added

- Minecraft 1.19.3 support.
- Added tripod reset keybind (hold and press a hotbar key to reset a tripod).
- Made setting names translatable.

### Fixed

- Fixed Freecam being pushed by pistons.
- (Hopefully) fixed teleportation glitch.

## [1.1.6] - 2022-09-11

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-mc1.19),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-forge-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-mc1.18),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-forge-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-mc1.17),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-forge-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-mc1.16),
                  [Forge](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.6-forge-mc1.16).

### Added

- Initial forge release.

### Fixed

- Fixed Show Player setting.
- Fixed tripods casting shadows when Iris is installed.
- Fixed error in Italian translations.

## [1.1.5] - 2022-08-28

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.5-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.5-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.5-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.5-mc1.16).

### Added

- Added Chinese and Italian translations.

### Fixed

- Fixed some FPS issues.
- Fixed water/ladders affecting flight speed.

## [1.1.4] - 2022-07-21

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.4-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.4-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.4-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.4-mc1.16).

### Changed

- Renamed interaction mode 'Freecam' setting to 'Camera'.
- Made warnings in setting descriptions red for visibility.

### Fixed

- Interaction mode 'Player' now respects the allow interaction setting.

## [1.1.3] - 2022-07-20

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.3-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.3-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.3-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.3-mc1.16).

### Added

- Added new 'Interaction Mode' setting.

### Fixed

- Fixed tripods getting stuck if the player leaves the dimension.
- Fixed incorrect toggle message in 1.18.

## [1.1.2] - 2022-07-06

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.2-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.2-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.2-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.2-mc1.16).

### Fixed

- Fixed freecam pose changing when clipping through blocks.

## [1.1.1] - 2022-07-05

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.1-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.1-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.1-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.1-mc1.16).

### Fixed

- Fixed player going dark if freecam is in an area with low light.
- Fixed getting stuck when entering freecam in 1-block-tall spaces with noclip disabled.

## [1.1.0] - 2022-07-04

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.0-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.0-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.0-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.1.0-mc1.16).

### Added

- Added separate setting for tripod notifications.

### Fixed

- Fixed Optifabric incompatibility.

## [1.0.9] - 2022-06-26

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.9-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.9-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.9-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.9-mc1.16).

### Added

- Added 'tripod' functionality.

### Fixed

- Mouse clicks now come from the player rather than the freecam entity when player control is enabled.
- You no longer need to exit and re-enter freecam for the 'Show Hand' setting to take effect.
- Fixed night vision not working underwater while in freecam.
- Fixed crash when using items in freecam in 1.16/1.17.

## [1.0.8] - 2022-06-10

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.8-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.8-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.8-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.8-mc1.16).

### Changed

- Changed default bind to F4 (X is taken by default).
- Improved Freeze Player setting.
- Changed setting descriptions.

### Fixed

- Fixed Freeze Player setting preventing movement while Player Control is active.
- Fixed crash on launch.

## [1.0.7] - 2022-06-09

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.7-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.7-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.7-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.7-mc1.16).

### Added

- Added 'Freeze Player' option.

### Fixed

- Fixed crash on launch.

## [1.0.6] - 2022-06-08

### Platforms

- Minecraft 1.19: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.6-mc1.19).
- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.6-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.6-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.6-mc1.16).

### Added

- Minecraft 1.19 support.

### Fixed

- Fixed night vision not working when using Iris.
- Disabling 'Allow Interaction' no longer blocks eating/drinking in freecam.
- Fixed item use animations not playing in freecam.
- Fixed being able to attack yourself (again).

## [1.0.5] - 2022-05-14

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.5-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.5-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.5-mc1.16).

### Fixed

- Fixed chunk borders highlighting the wrong chunk if 'Show Player' is enabled.
- Arm animations no longer play when 'Allow Interaction' is disabled.

## [1.0.4] - 2022-04-11

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.4-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.4-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.4-mc1.16).

### Fixed

- Fixed Freecam entity projecting a shadow when Iris is installed.

## [1.0.3] - 2022-04-05

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.3-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.3-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.3-mc1.16).

### Fixed

- Activating sprint in default flight mode now speeds you up.
- Fixed fov changing when touching ground with noclip disabled.

## [1.0.2] - 2022-04-03

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.2-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.2-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.2-mc1.16).

### Changed

- Allow Interaction is now disabled by default.

### Fixed

- Fixed being able to break blocks with Allow Interaction disabled if player is in creative.
- Fixed being able to open chests with Allow Interaction disabled.

## [1.0.1] - 2022-04-02

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.1-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.1-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.1-mc1.16).

### Added

- Added 'NoClip' option for toggling ability to travel through blocks.
- Added link to issues page in Mod Menu.

### Changed

- Made vertical speed default to 1.0.

### Fixed

- Fixed hand not moving when the camera is turned.
- Fixed freecam disabling randomly.

## [1.0.0] - 2022-03-18

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.0-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.0-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/1.0.0-mc1.16).

### Added

- Added 'Control Player' keybind that allows you to control your player while freecam is enabled.
- Added links to Curseforge and GitHub pages.

### Changed

- Merged 'Allow Breaking Blocks' and 'Allow Entity Interaction' settings into 'Allow Interaction' setting.

### Removed

- Removed 'Enable Message' and 'Disable Message' settings.

## [0.4.9] - 2022-03-14

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.9-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.9-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.9-mc1.16).

### Changed

- Holding jump and sneak at the same time will now make you hover in place.
- Player now remains sneaking if freecam is enabled while sneak is held.

## [0.4.8] - 2022-03-03

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.8-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.8-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.8-mc1.16).

### Added

- Added 'Disable on Damage' setting.

### Changed

- Made Freecam disable when switching dimensions/respawning.
- Reordered settings.

## [0.4.7] - 2022-02-27

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.7-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.7-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.7-mc1.16).

### Fixed

- Fixed OptiFabric crash.

## [0.4.6] - 2022-02-26

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.6-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.6-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.6-mc1.16).

### Fixed

- Fixed crash if horizontal speed is set to 0.
- Fixed incorrect arm lighting.

## [0.4.5] - 2022-02-25

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.5-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.5-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.5-mc1.16).

### Added

- Added back Flight Mode option.

## [0.4.4.1] - 2022-02-12

### Platforms

- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.4.1-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.4.1-mc1.16).

### Fixed

- Fixes mixin java compatibility level.

## [0.4.4] - 2022-02-12

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.4-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.4-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.4-mc1.16).

### Changed

- Made entities and block entities outside of entity view distance render while freecam is enabled.

## [0.4.3] - 2022-02-11

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.3-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.3-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.3-mc1.16).

### Fixed

- Ok Zoomer compatibility.

## [0.4.2] - 2022-02-10

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.2-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.2-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.2-mc1.16).

### Added

- Added 'Allow Entity Interaction' setting.

## [0.4.1] - 2022-02-10

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.1-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.1-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.1-mc1.16).

### Fixed

- Fixes for 'Allow Breaking Blocks'.

## [0.4.0] - 2022-02-10

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.0-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.0-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.4.0-mc1.16).

### Added

- Added an option to toggle block breaking while in freecam.
- Added an option to configure vertical speed independently from horizontal speed.

### Changed

- Rewritten for baritone compatibility.

### Removed

- Removed vanilla flight option.

## [0.3.5] - 2022-01-10

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.5-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.5-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.5-mc1.16).

### Fixed

- Clone now updates its vehicle when the player rides a new entity.
- Clone now shows damage when the player is hurt.

## [0.3.4] - 2021-12-29

### Platforms

- Minecraft 1.18: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.4-mc1.18).
- Minecraft 1.17: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.4-mc1.17).
- Minecraft 1.16: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.4-mc1.16).

### Changed

- Third person now gets toggled off upon entering freecam.
- Freecam now disables on dimension change.
- Made compatible with 1.18.x.
- Made compatible with 1.17.x.
- Made compatible with 1.16.2-1.16.5.

### Removed

- You can no longer enter third person person while in freecam.

### Fixed

- You no longer take knockback while in freecam.
- Clone position is now updated when player position is updated by server.

## [0.3.3] - 2021-12-25

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.3-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.3-mc1.17.1).
- Minecraft 1.16.5: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.3-mc1.16.5).

### Added

- Minecraft 1.16 support.

### Fixed

- Fixes item pickup sounds being blocked.

## [0.3.2] - 2021-12-21

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.2-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.2-mc1.17.1).

### Changed

- Improved config screen.

### Fixed

- Fixed movement bug.

## [0.3.1] - 2021-12-19

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.1-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3.1-mc1.17.1).

### Fixed

- Fixed crash on disconnect if freecam is enabled.

## [0.3] - 2021-12-17

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.3-mc1.17.1).

### Changed

- Item pickup animations now target clone if showClone is enabled.
- Clone now mirrors player hand swings.
- Clone now mirrors player held item.

### Fixed

- Fixed yaw resetting after using freecam in a vehicle.

## [0.2.5] - 2021-12-16

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.5-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.5-mc1.17.1).

### Added

- Added ability to switch between modded and vanilla flight modes.

### Changed

- Merged horizontal and vertical speed into a single setting.

### Fixed

- Now restores limb angles on disable.

## [0.2.4] - 2021-12-14

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.4-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.4-mc1.17.1).

### Fixed

- Visual fixes.

## [0.2.3] - 2021-12-14

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.3-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.3-mc1.17.1).

### Fixed

- Bugfixes.

## [0.2.2] - 2021-12-14

### Platforms

- Minecraft 1.18.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.2-mc1.18.1).
- Minecraft 1.17.1: [Fabric](https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.2-mc1.17.1).

### Added

- Minecraft 1.18 support.
- Minecraft 1.17 support.

[Unreleased]: https://github.com/MinecraftFreecam/Freecam/compare/1.2.3...HEAD
[1.2.3]: https://github.com/MinecraftFreecam/Freecam/compare/1.2.2-mc1.20...1.2.3
[1.2.2]: https://github.com/MinecraftFreecam/Freecam/compare/1.2.1.1-mc1.20...1.2.2-mc1.20
[1.2.1]: https://github.com/MinecraftFreecam/Freecam/compare/1.2.0-mc1.20...1.2.1-mc1.20
[1.2.0]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.10-mc1.20...1.2.0-mc1.20
[1.1.10]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.9-mc1.19...1.1.10-mc1.20
[1.1.9]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.8-mc1.19...1.1.9-mc1.19
[1.1.8]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.7-mc1.19...1.1.8-mc1.19
[1.1.7]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.6-mc1.19...1.1.7-mc1.19
[1.1.6]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.5-mc1.19...1.1.6-mc1.19
[1.1.5]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.4-mc1.19...1.1.5-mc1.19
[1.1.4]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.3-mc1.19...1.1.4-mc1.19
[1.1.3]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.2-mc1.19...1.1.3-mc1.19
[1.1.2]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.1-mc1.19...1.1.2-mc1.19
[1.1.1]: https://github.com/MinecraftFreecam/Freecam/compare/1.1.0-mc1.19...1.1.1-mc1.19
[1.1.0]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.9-mc1.19...1.1.0-mc1.19
[1.0.9]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.8-mc1.19...1.0.9-mc1.19
[1.0.8]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.7-mc1.19...1.0.8-mc1.19
[1.0.7]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.6-mc1.19...1.0.7-mc1.19
[1.0.6]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.5-mc1.18...1.0.6-mc1.19
[1.0.5]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.4-mc1.18...1.0.5-mc1.18
[1.0.4]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.3-mc1.18...1.0.4-mc1.18
[1.0.3]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.2-mc1.18...1.0.3-mc1.18
[1.0.2]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.1-mc1.18...1.0.2-mc1.18
[1.0.1]: https://github.com/MinecraftFreecam/Freecam/compare/1.0.0-mc1.18...1.0.1-mc1.18
[1.0.0]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.9-mc1.18...1.0.0-mc1.18
[0.4.9]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.8-mc1.18...0.4.9-mc1.18
[0.4.8]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.7-mc1.18...0.4.8-mc1.18
[0.4.7]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.6-mc1.18...0.4.7-mc1.18
[0.4.6]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.5-mc1.18...0.4.6-mc1.18
[0.4.5]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.4.1-mc1.17...0.4.5-mc1.18
[0.4.4]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.3-mc1.18...0.4.4-mc1.18
[0.4.3]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.2-mc1.18...0.4.3-mc1.18
[0.4.2]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.1-mc1.18...0.4.2-mc1.18
[0.4.1]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.0-mc1.18...0.4.1-mc1.18
[0.4.0]: https://github.com/MinecraftFreecam/Freecam/compare/0.3.5-mc1.18...0.4.0-mc1.18
[0.3.5]: https://github.com/MinecraftFreecam/Freecam/compare/0.3.4-mc1.18...0.3.5-mc1.18
[0.3.4]: https://github.com/MinecraftFreecam/Freecam/compare/0.3.3-mc1.18.1...0.3.4-mc1.18
[0.3.3]: https://github.com/MinecraftFreecam/Freecam/compare/0.3.2-mc1.18.1...0.3.3-mc1.18.1
[0.3.2]: https://github.com/MinecraftFreecam/Freecam/compare/0.3.1-mc1.18.1...0.3.2-mc1.18.1
[0.3.1]: https://github.com/MinecraftFreecam/Freecam/compare/0.3-mc1.18.1...0.3.1-mc1.18.1
[0.2.5]: https://github.com/MinecraftFreecam/Freecam/compare/0.2.4-mc1.18.1...0.2.5-mc1.18.1
[0.2.4]: https://github.com/MinecraftFreecam/Freecam/compare/0.2.3-mc1.18.1...0.2.4-mc1.18.1
[0.2.3]: https://github.com/MinecraftFreecam/Freecam/compare/0.2.2-mc1.18.1...0.2.3-mc1.18.1
[0.2.2]: https://github.com/MinecraftFreecam/Freecam/releases/tag/0.2.2-mc1.18.1
[0.3]: https://github.com/MinecraftFreecam/Freecam/compare/0.2.5-mc1.18.1...0.3-mc1.18.1
[0.4.4.1]: https://github.com/MinecraftFreecam/Freecam/compare/0.4.4-mc1.18...0.4.4.1-mc1.17
[1.2.1.1]: https://github.com/MinecraftFreecam/Freecam/compare/1.2.1-mc1.20...1.2.1.1-mc1.20
