package net.xolt.freecam.config.controller;

import net.xolt.freecam.config.model.ModConfigDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigControllerRegistry {
    private ConfigControllerRegistry() {}

    private static final Map<Class<?>, ConfigController<?>> CONTROLLERS = new ConcurrentHashMap<>();

    public static void init() {
        register(ModConfigDTO.class, SingletonModConfigController.INSTANCE);
    }

    public static <T> ConfigController<T> get(Class<T> configClass) {
        @SuppressWarnings("unchecked")
        ConfigController<T> controller = (ConfigController<T>) CONTROLLERS.get(configClass);
        if (controller == null) {
            throw new IllegalArgumentException("No controller registered for " + configClass.getSimpleName());
        }
        return controller;
    }

    public static <T> void register(Class<T> configClass, ConfigController<T> controller) {
        CONTROLLERS.put(configClass, controller);
    }
}
