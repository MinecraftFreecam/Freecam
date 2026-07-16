package net.xolt.freecam.config.controller;

import net.xolt.freecam.config.model.ModConfigDTO;
import net.xolt.freecam.config.model.ModConfigImpl;

import java.util.ArrayList;
import java.util.List;

public class ModConfigController implements ConfigController<ModConfigImpl> {

    private final ConfigController<ModConfigDTO> controller;
    private final List<Runnable> listeners = new ArrayList<>();
    private ModConfigImpl adapter;
    private final ModConfigImpl defaultAdapter;

    public ModConfigController(ConfigController<ModConfigDTO> dtoController) {
        this.controller = dtoController;
        this.defaultAdapter = new ModConfigImpl(new ModConfigDTO());
        this.controller.registerListener(this::onChange);
    }

    private void onChange() {
        adapter = new ModConfigImpl(controller.getConfig());
        listeners.forEach(Runnable::run);
    }

    @Override
    public ModConfigImpl getConfig() {
        return adapter;
    }

    @Override
    public ModConfigImpl getDefaults() {
        return defaultAdapter;
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
