---
default: patch
---

# Prevent collision with Sable sub-levels

Freecam only ignored collisions it could see through `BlockState#getCollisionShape`, so collisions handled in other places could still stop the camera.

Sable resolves collisions with its sub-levels separately, which meant the camera entity would still collide with them when all collisions were ignored.

Fixes [#464](https://github.com/MinecraftFreecam/Freecam/issues/464).
