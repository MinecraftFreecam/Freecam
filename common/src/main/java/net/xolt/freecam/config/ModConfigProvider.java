package net.xolt.freecam.config;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.util.SingleInstanceServiceLoader;

public abstract class ModConfigProvider {

    private static ModConfigProvider self = null;

    protected ModConfigProvider() {}

    public abstract MCAwareModConfig getConfig();
    public abstract void setupConfig();
    public abstract Screen createConfigScreen(Screen parent);

    private static ModConfigProvider self() {
        if (self == null) self = SingleInstanceServiceLoader.get(ModConfigProvider.class);
        return self;
    }

    public static void init() {
        self().setupConfig();
    }

    public static MCAwareModConfig instance() {
        return self().getConfig();
    }

    public static Screen getConfigScreen(Screen parent) {
        return self().createConfigScreen(parent);
    }
}
