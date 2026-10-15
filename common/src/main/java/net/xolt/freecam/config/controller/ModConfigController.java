package net.xolt.freecam.config.controller;

import net.xolt.freecam.config.model.ModConfigDTO;
import net.xolt.freecam.config.model.ModConfigImpl;

import java.util.ArrayList;
import java.util.List;

public class ModConfigController implements ConfigController<ModConfigImpl> {

    private final ConfigController<ModConfigDTO> controller;
    private final List<Runnable> listeners = new ArrayList<>();
    private final ModConfigImpl defaultConfig;
    private ModConfigImpl config;

    public ModConfigController(ConfigController<ModConfigDTO> dtoController) {
        this.controller = dtoController;
        this.defaultConfig = new ModConfigImpl(new ModConfigDTO());
        this.controller.registerListener(this::onChange);
    }

    private void onChange() {
        config = new ModConfigImpl(controller.getConfig());
        listeners.forEach(Runnable::run);
    }

    @Override
    public ModConfigImpl getConfig() {
        return config;
    }

    @Override
    public ModConfigImpl getDefaults() {
        return defaultConfig;
    }

    @Override
    public void load() {
        controller.load();
    }

    @Override
    public void save() {
        controller.save();
    }

    @Override
    public void registerListener(Runnable listener) {
        listeners.add(listener);
    }
}
