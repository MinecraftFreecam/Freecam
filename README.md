# Freecam

This mod allows you to control your camera separately from your player. While it is enabled, you can fly around and travel through blocks within your render distance. Disabling it will restore you to your original position. This can be useful for quickly inspecting builds and exploring your world. 

This mod works in multiplayer, but may be considered cheating on some servers, so use it at your own risk.

## Keybinds

|Name|Description|Default Bind|
|-|-|-|
|Toggle Freecam|Enables/disables Freecam|`F4`|
|Control Player|Transfers control back to your player, but retains your current perspective (Can only be used while Freecam is active.)|`Unbound`|
|Reset Tripod|Resets a tripod\* camera when pressed in combination with any of the hotbar keys|`Unbound`|

\*The freecam bind can also be used in conjunction with any of the hotbar keys (`F4` + `1`...`9`) to enter "tripod" mode. This enables you to set up multiple cameras with differing perspectives, and switch between them at will.

## Settings

(Configurable via [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) or the config file)

|Name|Description|Default Value|
|-|-|-|
|Flight Mode|The type of flight used by freecam.<br /><br />**Options:**<br />- `DEFAULT` Static velocity with no drifting<br />- `CREATIVE` Vanilla creative flight|`DEFAULT`|
|Interaction Mode|The source of block/entity interactions.<br /><br />**Options:**<br />- `CAMERA` Interactions come from the camera<br />- `PLAYER` Interactions come from the player|`CAMERA`|
|Horizontal Speed|The horizontal speed of freecam.|`1.0`|
|Vertical Speed|The vertical speed of freecam.|`1.0`|
|No Clip|Whether you can travel through blocks in freecam.|`true`|
|Freeze Player|Prevents player movement while freecam is active.<br />**WARNING: Multiplayer usage not advised.**|`false`|
|Allow Interaction|Whether you can interact with blocks/entities in freecam.<br />**WARNING: Multiplayer usage not advised.**|`false`|
|Disable on Damage|Disables freecam when damage is received.|`true`|
|Show Player|Shows your player in its original position.|`true`|
|Show Hand|Whether you can see your hand in freecam.|`false`|
|Freecam Notifications|Notifies you when entering/exiting freecam.|`true`|
|Tripod Notifications|Notifies you when entering/exiting tripod cameras.|`true`|

## Requirements

### Fabric
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) (Optional for easier configuration)

### Forge
- None

Curseforge page [here](https://www.curseforge.com/minecraft/mc-mods/free-cam).
