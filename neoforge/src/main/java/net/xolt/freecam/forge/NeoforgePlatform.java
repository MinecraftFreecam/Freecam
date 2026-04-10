package net.xolt.freecam.forge;

//? if neoforge: >=21
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.xolt.freecam.ModPlatform;

public class NeoforgePlatform implements ModPlatform {

    @Override
    public boolean isModLoaded(String modId) {
        //~ if neoforge: >=21 'net.neoforged.fml.loading.LoadingModList.get()' -> 'FMLLoader.getCurrent().getLoadingModList()'
        return FMLLoader.getCurrent().getLoadingModList().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
    }
}
