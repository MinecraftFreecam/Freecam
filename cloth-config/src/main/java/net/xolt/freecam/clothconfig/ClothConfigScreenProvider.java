package net.xolt.freecam.clothconfig;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.config.ConfigScreenProvider;
import net.xolt.freecam.config.controller.ConfigControllerRegistry;
import net.xolt.freecam.config.model.ModConfigDTO;
import org.jetbrains.annotations.Nullable;

import static java.lang.Thread.currentThread;

public class ClothConfigScreenProvider implements ConfigScreenProvider {

    private @Nullable ModConfigScreenFactory factory;

    /**
     * Init {@link #factory} lazily, so that {@link ModConfigScreenFactory} is not class-loaded immediately.
     * <p>
     * This gives consumers an opportunity to test {@link ClothConfigScreenProvider#isAvailable()} before use.
     * @return {@link #factory} initialized
     */
    private ModConfigScreenFactory factory() {
        if (factory == null) {
            factory = new ModConfigScreenFactory(ConfigControllerRegistry.get(ModConfigDTO.class));
        }
        return factory;
    }

    @Override
    public String getName() {
        return "Cloth Config Freecam GUI";
    }

    @Override
    public Screen getConfigScreen(@Nullable Screen parent) {
        return factory().getConfigScreen(parent);
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder", false, currentThread().getContextClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}
