package net.xolt.freecam.config;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.util.SingleInstanceServiceLoader;

import static net.xolt.freecam.Freecam.MC;

public interface ConfigScreenProvider {

    Screen getConfigScreen(Screen parent);

    default void openConfigScreen() {
        openConfigScreen(MC.screen);
    }

    default void openConfigScreen(Screen parent) {
        MC.setScreen(getConfigScreen(parent));
    }

    static ConfigScreenProvider instance() {
        return Holder.INSTANCE;
    }

    class Holder {
        private Holder() {}

        private static final ConfigScreenProvider INSTANCE = SingleInstanceServiceLoader.get(ConfigScreenProvider.class);
    }
}
