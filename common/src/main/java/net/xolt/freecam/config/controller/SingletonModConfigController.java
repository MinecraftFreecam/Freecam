package net.xolt.freecam.config.controller;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.model.ConfigLoader;
import net.xolt.freecam.config.model.GsonConfigLoader;
import net.xolt.freecam.config.model.ModConfigDTO;
import net.xolt.freecam.config.model.ModConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SingletonModConfigController implements ConfigController<ModConfigImpl> {

    static final SingletonModConfigController INSTANCE = new SingletonModConfigController();

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonModConfigController.class);

    private final ConfigLoader<ModConfigDTO> loader = new GsonConfigLoader<>(ModConfigDTO.class, Freecam.MOD_ID);
    private final ModConfigImpl defaults = new ModConfigImpl(new ModConfigDTO());
    private ModConfigImpl config;

    private SingletonModConfigController() {}

    @Override
    public ModConfigImpl getConfig() {
        if (config == null) {
            LOGGER.warn("Config used before load");
            return getDefaults();
        }
        return config;
    }

    @Override
    public ModConfigImpl getDefaults() {
        return defaults;
    }

    @Override
    public void load() {
        try {
            config = new ModConfigImpl(loader.read());
        } catch (Exception e) {
            LOGGER.error("Failed to load config, using defaults", e);
            config = new ModConfigImpl(new ModConfigDTO());
        }
    }

    @Override
    public void save() {
        ModConfigDTO data = getConfig().getData();
        try {
            loader.write(data);
        } catch (Exception e) {
            LOGGER.error("Failed to save config", e);
            // TODO: Consider propagating an error to the GUI
            return;
        }
        config =  new ModConfigImpl(data);
    }
}
