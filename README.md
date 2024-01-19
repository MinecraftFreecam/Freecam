# Freecam
![CI](https://github.com/MinecraftFreecam/Freecam/actions/workflows/main.yml/badge.svg?event=push)
[![Crowdin](https://badges.crowdin.net/freecam/localized.svg)](https://crowdin.com/project/freecam)

This mod allows you to control your camera separately from your player. While it is enabled, you can fly around and travel through blocks within your render distance. Disabling it will restore you to your original position. This can be useful for quickly inspecting builds and exploring your world. 

This mod works in multiplayer, but may be considered cheating on some servers, so use it at your own risk.

## Translation

You can help [translate Freecam on Crowdin](https://crowdin.com/project/freecam).
If you're unfamiliar with Crowdin, you can read their [getting started as a volunteer translator guide](https://support.crowdin.com/enterprise/getting-started-for-volunteers).

If you'd like to translate a language we don't currently target, please [open an issue](https://github.com/MinecraftFreecam/Freecam/issues/new) asking us to enable the language.

## Keybinds

| Name           | Description                                                                                                             | Default Bind |
|----------------|-------------------------------------------------------------------------------------------------------------------------|--------------|
| Toggle Freecam | Enables/disables Freecam                                                                                                | `F4`         |
| Config GUI     | Opens the settings screen.                                                                                              | `Unbound`    |
| Control Player | Transfers control back to your player, but retains your current perspective (Can only be used while Freecam is active.) | `Unbound`    |
| Reset Tripod   | Resets a tripod\* camera when pressed in combination with any of the hotbar keys                                        | `Unbound`    |

\*The freecam bind can also be used in conjunction with any of the hotbar keys (`F4` + `1`...`9`) to enter "tripod" mode. This enables you to set up multiple cameras with differing perspectives, and switch between them at will.

## Movement Options

| Name             | Description                                                                                                                                                 | Default Value |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| Flight Mode      | The type of flight used by freecam.<br /><br />**Options:**<br />- `DEFAULT` : Static velocity with no drifting<br />- `CREATIVE` : Vanilla creative flight | `DEFAULT`     |
| Horizontal Speed | The horizontal speed of freecam.                                                                                                                            | `1.0`         |
| Vertical Speed   | The vertical speed of freecam.                                                                                                                              | `1.0`         |

## Collision Options

| Name                           | Description                                                                                          | Default Value |
|--------------------------------|------------------------------------------------------------------------------------------------------|---------------|
| Ignore Transparent Blocks      | Allows travelling through transparent blocks in freecam.                                             | `true`        |
| Ignore Openable Blocks         | Allows travelling through doors/trapdoors/gates in freecam.                                          | `true`        |
| Ignore All Collision           | Allows travelling through all blocks in freecam.                                                     | `true`        |
| Always Check Initial Collision | Whether **Initial Perspective** should check for collision, even when using **Ignore All Collision** | `false`       |

## Visual Options

| Name                | Description                                                                                                                                                                                                                                         | Default Value |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| Initial Perspective | The initial perspective of the camera.<br /><br />**Options:**<br />- `FIRST_PERSON` : The player's perspective<br />- `THIRD_PERSON` : Behind the player<br />- `THIRD_PERSON_MIRROR` : In front of the player<br />- `INSIDE` : Inside the player | `INSIDE`      |
| Show Player         | Shows your player in its original position.                                                                                                                                                                                                         | `true`        |
| Show Hand           | Whether you can see your hand in freecam.                                                                                                                                                                                                           | `false`       |
| Full Brightness     | Increases brightness while in freecam.                                                                                                                                                                                                              | `false`       |
| Show Submersion Fog | Whether you see a fog overlay underwater, in lava, or powdered snow.                                                                                                                                                                                | `false`       |

## Utility Options

| Name              | Description                                                                                                                                                              | Default Value |
|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| Disable on Damage | Disables freecam when damage is received.                                                                                                                                | `true`        |
| Freeze Player     | Prevents player movement while freecam is active.<br />**WARNING: Multiplayer usage not advised.**                                                                       | `false`       |
| Allow Interaction | Whether you can interact with blocks/entities in freecam.<br />**WARNING: Multiplayer usage not advised.**                                                               | `false`       |
| Interaction Mode  | The source of block/entity interactions.<br /><br />**Options:**<br />- `CAMERA` : Interactions come from the camera<br />- `PLAYER` : Interactions come from the player | `CAMERA`      |

## Notification Options

| Name                  | Description                                             | Default Value |
|-----------------------|---------------------------------------------------------|---------------|
| Freecam Notifications | Notifies you when entering/exiting freecam.             | `true`        |
| Tripod Notifications  | Notifies you when entering/exiting tripod cameras.<br/> | `true`        |

## Requirements

### Fabric
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) (Optional)

### Forge
- None

Curseforge page [here](https://www.curseforge.com/minecraft/mc-mods/free-cam).
