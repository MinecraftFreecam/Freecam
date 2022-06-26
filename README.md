# Freecam

This mod allows you to control your camera separately from your player. While it is enabled, you can fly around and travel through blocks within your render distance. Disabling it will restore you to your original position. This can be useful for quickly inspecting builds and exploring your world. 

This mod works in multiplayer, but may be considered cheating on some servers, so use it at your own risk.

## Keybinds

|Name|Description|Default Bind|
|-|-|-|
|Toggle Freecam|Enables/disables Freecam|`F4`|
|Control Player|Transfers control back to your player, but retains your current perspective (Can only be used while Freecam is active.)|`Unbound`|

## Settings

(Configurable via [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) or the config file)

|Name|Description|Default Value|
|-|-|-|
|Flight Mode|The type of flight used by freecam.<br /><br />**Options:**<br />- `DEFAULT` Static velocity with no drifting<br />- `CREATIVE` Vanilla creative flight|`DEFAULT`|
|Horizontal Speed|The horizontal speed of freecam.|`1.0`|
|Vertical Speed|The vertical speed of freecam.|`1.0`|
|NoClip|Whether you can travel through blocks in freecam.|`true`|
|Freeze Player|Prevents player movement while freecam is active.<br />**WARNING: Multiplayer usage not advised.**|`false`|
|Allow Interaction|Whether you can interact with blocks/entities in freecam.<br />**WARNING: Multiplayer usage not advised.**|`false`|
|Disable on Damage|Disables freecam when damage is received.|`true`|
|Show Player|Shows your player in its original position.|`true`|
|Show Hand|Whether you can see your hand in freecam.|`false`|
|Notify|Whether action bar notifications are displayed.|`true`|

## Requirements
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) (Optional for easier configuration)

Curseforge page [here](https://www.curseforge.com/minecraft/mc-mods/free-cam).
