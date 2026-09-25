# Freecam
[![Crowdin](https://badges.crowdin.net/freecam/localized.svg)](https://crowdin.com/project/freecam)

<!-- website:start id="description" -->

This mod allows you to control your camera separately from your player. While it is enabled, you can fly around and travel through blocks within your render distance. Disabling it will restore you to your original position. This can be useful for quickly inspecting builds and exploring your world. 

This mod works in multiplayer, but may be considered cheating on some servers.
It is your responsibility to check **all** relevant rules **before** using this mod.

<!-- website:stop -->

Freecam advertises networking channels so servers can send temporary restrictions. Servers may also detect the mod through other Minecraft or mod-loader behavior.

To paraphrase our license, we are not responsible for any damages or liability related to Freecam.
Among other things, this means it's not our fault if you get banned from a server.
Here's the [full license](./LICENSE).

## Contributing

<!-- website:start id="contribution" -->

You can contribute to Freecam by opening or investigating issues, translating the mod to your language, or contributing code.
See the [contributing guide](https://github.com/MinecraftFreecam/Freecam/blob/main/CONTRIBUTING.md) for more detail!

<!-- website:stop -->
<!-- website:start id="config" -->

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

<!-- website:stop -->

## Server policies

Servers can temporarily restrict Freecam, ignoring collision, full brightness, and interactions from the camera. Received rules reset on disconnect and never overwrite your saved preferences. Freecam also supports servers using [AntiFreecam](https://github.com/Kesuaheli/AntiFreecam).

For singleplayer, configuring the policy options under **Multiplayer Options** in the settings menu will affect players connected via open-to-LAN. Policies apply to compatible connected clients; saving policy changes updates them.
By default, you are not affected by your own policies: enable **Apply Policies to Me** to also restrict yourself in singleplayer worlds.

For a dedicated Fabric, Forge, or NeoForge server, install Freecam on the server and configure the `serverPolicy` section of `config/freecam.json`. You can copy the same file from a client. Restart the dedicated server after editing it. All permissions default to `true`:

```json
{
  "serverPolicy": {
    "allowFreecam": true,
    "collision": {
      "allowIgnoring": false
    },
    "allowFullbright": false,
    "allowCameraInteractions": true
  }
}
```

Clients without Freecam can still join and clients with a different implementation of freecam are unaffected.

Instead of installing Freecam on your server, a custom server plugin can send a compatible UTF-8 JSON payload on the `freecam:server_config` channel. The payload schema is the same JSON that's saved in a `config/freecam.json` config, without the outer `serverPolicy` wrapper. Each valid message replaces the previous policy; omitted fields allow the feature.

## Requirements

### Fabric
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) (Optional)

### Forge
- None
