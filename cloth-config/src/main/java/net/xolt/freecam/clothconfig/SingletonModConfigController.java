package net.xolt.freecam.clothconfig;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.clothconfig.model.GsonConfigLoader;
import net.xolt.freecam.clothconfig.model.ModConfigDTO;
import net.xolt.freecam.config.model.ConfigController;
import net.xolt.freecam.config.model.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SingletonModConfigController implements ConfigController<ModConfigDTO> {

    public static final SingletonModConfigController INSTANCE = new SingletonModConfigController();

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonModConfigController.class);

    private final ConfigLoader<ModConfigDTO> loader = new GsonConfigLoader<>(ModConfigDTO.class, Freecam.MOD_ID);
    private final ModConfigDTO defaults = new ModConfigDTO();
    private ModConfigDTO config;

    private SingletonModConfigController() {}

    @Override
    public ModConfigDTO getConfig() {
        if (config == null) {
            LOGGER.warn("Config used before load");
            return getDefaults();
        }
        return config;
    }

    @Override
    public ModConfigDTO getDefaults() {
        return defaults;
    }

    @Override
    public void load() {
        try {
            config = loader.read();
        } catch (Exception e) {
            LOGGER.error("Failed to load config, using defaults", e);
            config = new ModConfigDTO();
        }
    }

    @Override
    public void save() {
        ModConfigDTO config = getConfig();
        try {
            loader.write(config);
        } catch (Exception e) {
            LOGGER.error("Failed to save config", e);
            // TODO: Consider propagating an error to the GUI
            return;
        }
        config.onConfigChange();
    }
}
