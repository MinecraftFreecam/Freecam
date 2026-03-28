package net.xolt.freecam.clothconfig;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.config.ConfigScreenProvider;
import net.xolt.freecam.config.controller.ConfigControllerRegistry;
import net.xolt.freecam.config.model.ModConfigDTO;
import org.jetbrains.annotations.Nullable;

public class ClothConfigScreenProvider implements ConfigScreenProvider {

    private final ModConfigScreenFactory factory = new ModConfigScreenFactory(ConfigControllerRegistry.get(ModConfigDTO.class));

    @Override
    public Screen getConfigScreen(@Nullable Screen parent) {
        return factory.getConfigScreen(parent);
    }
}
