package net.xolt.freecam.config.gui;

import net.minecraft.client.gui.screens.Screen;

final class FallbackConfigScreenProvider implements ConfigScreenProvider {

    @Override
    public String getName() {
        return "fallback screen";
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        return new FallbackConfigScreen(parent);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
