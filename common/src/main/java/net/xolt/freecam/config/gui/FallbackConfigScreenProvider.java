package net.xolt.freecam.config.gui;

import net.minecraft.client.gui.screens.Screen;

import java.util.Collection;

final class FallbackConfigScreenProvider implements ConfigScreenProvider {

    private final Collection<OptionalProvider> supportedProviders;

    public FallbackConfigScreenProvider(Collection<OptionalProvider> supportedProviders) {
        this.supportedProviders = supportedProviders;
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        return new FallbackConfigScreen(parent, supportedProviders);
    }

    @Override
    public String getName() {
        return "fallback screen";
    }
}
