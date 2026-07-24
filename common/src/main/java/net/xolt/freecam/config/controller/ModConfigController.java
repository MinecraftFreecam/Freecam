package net.xolt.freecam.config.controller;

import net.xolt.freecam.config.model.ModConfigDTO;
import net.xolt.freecam.config.model.ModConfigDTOAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModConfigController implements ConfigController<ModConfigDTOAdapter> {

    private final ConfigController<ModConfigDTO> controller;
    private final List<Runnable> listeners = new ArrayList<>();
    private ModConfigDTOAdapter adapter;
    private final ModConfigDTOAdapter defaultAdapter;

    public ModConfigController(ConfigController<ModConfigDTO> dtoController) {
        this.controller = dtoController;
        this.defaultAdapter = new ModConfigDTOAdapter(new ModConfigDTO());
        this.controller.registerListener(this::onChange);
    }

    private void onChange() {
        adapter = new ModConfigDTOAdapter(controller.getConfig());
        listeners.forEach(Runnable::run);
    }

    @Override
    public ModConfigDTOAdapter getConfig() {
        return adapter;
    }

    @Override
    public ModConfigDTOAdapter getDefaults() {
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
