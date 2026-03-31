package net.xolt.freecam.config.model;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

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
        JsonObject current = gson.toJsonTree(config).getAsJsonObject();
        JsonObject previous = config instanceof RawJsonHolder holder ? holder.getRawJson() : null;
        JsonObject merged = previous == null ? current : merge(current, previous);

        Files.createDirectories(filepath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            gson.toJson(merged, writer);
        }
    }

    @Override
    public T read() throws Exception {
        if (Files.exists(filepath)) {
            return load(filepath);
        }

        // Attempt to migrate old .json5 configs to .json
        if (Files.exists(legacyFilepath)) {
            LOGGER.info("{} not found, attempting to migrate legacy config {}", filepath.getFileName(), legacyFilepath.getFileName());
            try {
                return load(legacyFilepath);
            } catch (Exception e) {
                LOGGER.warn("Failed to migrate legacy config {}, falling back to defaults", legacyFilepath.getFileName(), e);
            }
        }

        // Return defaults if no config file exists
        return configClass.getConstructor().newInstance();
    }

    private T load(Path path) throws Exception {
        JsonObject rawJson;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            //~ if gson: >=2.8.6 'new JsonParser().parse' -> 'JsonParser.parseReader'
            rawJson = JsonParser.parseReader(reader).getAsJsonObject();
        }
        T result = gson.fromJson(rawJson, configClass);
        if (result instanceof RawJsonHolder holder) {
            holder.setRawJson(rawJson);
        }
        return result;
    }

    /**
     * Recursively preserve unknown fields from unrecognized config versions.
     * <p>
     * Merges keys from {@code previous} unless they are already present in {@code current}.
     * <p>
     * Merges recursively when both sides have a {@link JsonObject} at the same key.
     * Non-object fields present in {@code current} are always kept, taking precedence over conflicting fields in
     * {@code previous}.
     *
     * @return a new {@link JsonObject}
     */
    private JsonObject merge(JsonObject current, JsonObject previous) {
        JsonObject result = deepCopy(current).getAsJsonObject();
        for (Entry<String, JsonElement> entry : previous.entrySet()) {
            String key = entry.getKey();
            JsonElement previousValue = entry.getValue();
            JsonElement currentValue = current.get(key);

            if (currentValue == null) {
                // Unknown key: use previous value
                result.add(key, deepCopy(previousValue));
            } else if (currentValue.isJsonObject() && previousValue.isJsonObject()) {
                // Nested objects: merge recursively
                JsonObject object = merge(currentValue.getAsJsonObject(), previousValue.getAsJsonObject());
                result.add(key, object);
            }
            // Otherwise: current overwrites previous value
        }
        return result;
    }

    private JsonElement deepCopy(JsonElement element) {
        //? if gson: >=2.8.2 {
        return element.deepCopy();
        //? } else
        //return gson.fromJson(gson.toJson(element), JsonElement.class);
    }
}
