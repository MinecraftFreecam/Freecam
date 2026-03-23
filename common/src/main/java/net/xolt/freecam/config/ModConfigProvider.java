package net.xolt.freecam.config;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
//~ if cloth: >=21.11 'AutoConfig' -> 'AutoConfigClient'
import me.shedaniel.autoconfig.AutoConfigClient;

public class ModConfigProvider {

    public static MCAwareModConfig instance() {
        // TODO: ServiceLoader
        // TODO: init internally?
        return AutoConfigModConfig.INSTANCE;
    }

    public static void init() {
        AutoConfigModConfig.init();
    }

    public static Screen getConfigScreen(@Nullable Screen parent) {
        //~ if cloth: >=21.11 'AutoConfig.' -> 'AutoConfigClient.'
        return AutoConfigClient.getConfigScreen(AutoConfigModConfig.class, parent).get();
    }

    private ModConfigProvider() {}
}
