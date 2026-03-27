package net.xolt.freecam.config;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.util.SingleInstanceServiceLoader;

public interface ModConfigProvider {

    MCAwareModConfig getConfig();
    void setupConfig();
    Screen createConfigScreen(Screen parent);

    static void init() {
        Holder.INSTANCE.setupConfig();
    }

    static MCAwareModConfig instance() {
        return Holder.INSTANCE.getConfig();
    }

    static Screen getConfigScreen(Screen parent) {
        return Holder.INSTANCE.createConfigScreen(parent);
    }

    class Holder {
        private Holder() {}

        private static final ModConfigProvider INSTANCE = SingleInstanceServiceLoader.get(ModConfigProvider.class);
    }
}
