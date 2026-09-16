---
default: patch
---

# Tripod key combos not working in 26.3

26.3 moved from GLFW to SDL for input handling, which inadvertently broke our "tripod" keybindings (`F4` + hotbar slot number key).
