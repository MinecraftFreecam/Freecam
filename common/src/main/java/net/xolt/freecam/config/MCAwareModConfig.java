package net.xolt.freecam.config;

import net.minecraft.world.level.block.Block;

/**
 * Extends {@link ModConfig} with Minecraft-aware features.
 */
public interface MCAwareModConfig extends ModConfig {
    // FIXME: interface should not use MC classes
    boolean ignoreCollisionWith(Block block);
}
