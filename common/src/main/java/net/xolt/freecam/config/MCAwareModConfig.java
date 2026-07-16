package net.xolt.freecam.config;

import net.minecraft.world.level.block.Block;
import net.xolt.freecam.config.controller.ConfigControllerRegistry;
import net.xolt.freecam.config.model.ModConfigImpl;

/**
 * Extends {@link ModConfig} with Minecraft-aware features.
 */
public interface MCAwareModConfig extends ModConfig {

    static MCAwareModConfig get() {
        return ConfigControllerRegistry.get(ModConfigImpl.class).getConfig();
    }

    // FIXME: interface should not use MC classes
    boolean ignoreCollisionWith(Block block);
}
