package net.xolt.freecam.config.controller;

import net.xolt.freecam.config.model.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CoreConfigController<T> implements ConfigController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreConfigController.class);

    private final ConfigLoader<T> loader;
    private final List<Runnable> listeners = new ArrayList<>();
    private final T defaults;
    private final Supplier<T> defaultSupplier;
    private T config;

    public CoreConfigController(ConfigLoader<T> loader, Supplier<T> defaultSupplier) {
        this.loader = loader;
        this.defaults = defaultSupplier.get();
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public T getConfig() {
        if (config == null) {
            throw new IllegalStateException("Freecam config used before load");
        }
        return config;
    }

    @Override
    public T getDefaults() {
        return defaults;
    }

    @Override
    public void load() {
        try {
            config = loader.read();
        } catch (Exception e) {
            LOGGER.error("Failed to load config, using defaults", e);
            // TODO: Consider propagating an error to the GUI
            config = defaultSupplier.get();
        }
        listeners.forEach(Runnable::run);
    }

    @Override
    public void save() {
        try {
            loader.write(getConfig());
        } catch (Exception e) {
            LOGGER.error("Failed to save config", e);
        }
        listeners.forEach(Runnable::run);
    }

    @Override
    public void registerListener(Runnable listener) {
        listeners.add(listener);
    }
}
