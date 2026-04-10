package net.xolt.freecam.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.xolt.freecam.ModPlatform;

public class FabricPlatform implements ModPlatform {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
