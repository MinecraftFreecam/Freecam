package net.xolt.freecam.clothconfig;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.config.ConfigScreenProvider;
import net.xolt.freecam.config.MCAwareModConfig;
import net.xolt.freecam.config.ModConfigProvider;
import net.xolt.freecam.clothconfig.model.ModConfigDTO;
import net.xolt.freecam.config.model.ConfigController;
import org.jetbrains.annotations.Nullable;

public class ClothConfigProvider implements ModConfigProvider, ConfigScreenProvider {

    private final ConfigController<ModConfigDTO> controller = SingletonModConfigController.INSTANCE;
    private final ModConfigScreenFactory factory = new ModConfigScreenFactory(controller);

    @Override
    public MCAwareModConfig getConfig() {
        return controller.getConfig();
    }

    @Override
    public void setupConfig() {
        controller.load();
    }

    @Override
    public Screen getConfigScreen(@Nullable Screen parent) {
        return factory.getConfigScreen(parent);
    }
}
