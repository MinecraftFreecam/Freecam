package net.xolt.freecam.forge;

import dev.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;

import static net.xolt.freecam.BuildConfig.MODID;

@Mod(MODID)
public class FreecamForge {
    public FreecamForge() {
        // Register our event bus with Architectury
        EventBuses.registerModEventBus(MODID, FMLJavaModLoadingContext.get().getModEventBus());

        // Register our config screen with Forge
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) ->
            AutoConfig.getConfigScreen(ModConfig.class, parent).get()
        ));

        // Call our init
        Freecam.init();
    }
}
