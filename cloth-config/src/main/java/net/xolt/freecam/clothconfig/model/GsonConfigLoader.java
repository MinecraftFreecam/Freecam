package net.xolt.freecam.clothconfig.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.xolt.freecam.config.model.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonConfigLoader<T> implements ConfigLoader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonConfigLoader.class);

    private final Class<T> configClass;
    private final Path filepath;
    private final Path legacyFilepath;
    private final Gson gson;

    public GsonConfigLoader(Class<T> configClass, String name) {
        this(configClass, name, new GsonBuilder().setPrettyPrinting().create());
    }

    public GsonConfigLoader(Class<T> configClass, String name, Gson gson) {
        this.configClass = configClass;
        this.gson = gson;
        Path configDir = Minecraft.getInstance().gameDirectory.toPath().resolve("config");
        this.filepath = configDir.resolve(name + ".json");
        this.legacyFilepath = configDir.resolve(name + ".json5");
    }

    @Override
    public void write(T config) throws Exception {
        Files.createDirectories(filepath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            gson.toJson(config, writer);
        }
    }

    @Override
    public T read() throws Exception {
        if (Files.exists(filepath)) {
            try (BufferedReader reader = Files.newBufferedReader(filepath)) {
                return gson.fromJson(reader, configClass);
            }
        }

        // Attempt to migrate old .json5 configs to .json
        if (Files.exists(legacyFilepath)) {
            LOGGER.info("{} not found, attempting to migrate legacy config {}", filepath.getFileName(), legacyFilepath.getFileName());
            try (BufferedReader reader = Files.newBufferedReader(legacyFilepath)) {
                return gson.fromJson(reader, configClass);
            } catch (Exception e) {
                LOGGER.warn("Failed to migrate legacy config {}, falling back to defaults", legacyFilepath.getFileName(), e);
            }
        }

        // Return defaults if no config file exists
        return configClass.getConstructor().newInstance();
    }
}
