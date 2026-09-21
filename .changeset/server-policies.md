---
default: minor
---

Servers can temporarily restrict Freecam, clipping, full brightness, and interactions using a raw UTF-8 JSON plugin message on `freecam:server_config`.

The boolean fields `allowFreecam`, `allowClipping`, `allowFullbright`, and `allowInteract` default to `true` when omitted. Each valid message replaces the previous policy; invalid messages leave it unchanged. Restrictions reset on disconnect and never change the saved local configuration.

Supports AntiFreecam's `antifreecam:freecam_config_packet` boolean: `forceCollision=true` disables clipping, independently of JSON restrictions, and resets on disconnect.

Freecam can now be installed on dedicated servers. Configure `serverPolicy` in `config/freecam.json` (or copy that file from a client) and restart the server to apply it. The same four permissions can be edited under **Hosted Server Policy** in the client settings. Opening a world to LAN applies these rules to compatible connected clients, including the host; saving changes updates them during play. Unshared single-player worlds keep their personal settings. Clients without Freecam remain able to join.
