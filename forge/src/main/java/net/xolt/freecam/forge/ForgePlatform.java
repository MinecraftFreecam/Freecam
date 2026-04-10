package net.xolt.freecam.forge;

import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.xolt.freecam.ModPlatform;

public class ForgePlatform implements ModPlatform {

    @Override
    public boolean isModLoaded(String modId) {
        return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
    }
}
