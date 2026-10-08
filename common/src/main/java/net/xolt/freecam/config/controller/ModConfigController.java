package net.xolt.freecam.config.controller;

import net.xolt.freecam.config.model.ModConfigDTO;
import net.xolt.freecam.config.model.ModConfigImpl;
import net.xolt.freecam.network.ServerPolicies;

import java.util.ArrayList;
import java.util.List;

public class ModConfigController implements ConfigController<ModConfigImpl> {

    private final ConfigController<ModConfigDTO> controller;
    private final ServerPolicies serverPolicies;
    private final List<Runnable> listeners = new ArrayList<>();
    private final ModConfigImpl defaultConfig;
    private ModConfigImpl config;

    public ModConfigController(ConfigController<ModConfigDTO> dtoController, ServerPolicies serverPolicies) {
        this.controller = dtoController;
        this.serverPolicies = serverPolicies;
        this.defaultConfig = new ModConfigImpl(new ModConfigDTO(), serverPolicies);
        this.controller.registerListener(this::onChange);
    }

    private void onChange() {
        config = new ModConfigImpl(controller.getConfig(), serverPolicies);
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
