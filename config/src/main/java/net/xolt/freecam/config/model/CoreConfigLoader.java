package net.xolt.freecam.config.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CoreConfigLoader<T> implements ConfigLoader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreConfigLoader.class);

    private final Class<T> configClass;
    private final Path filepath;
    private final Path legacyFilepath;
    private final ConfigSerializer serializer;

    public CoreConfigLoader(ConfigSerializer serializer, Class<T> configClass, Path configDir, String name) {
        this.configClass = configClass;
        this.filepath = configDir.resolve(name + ".json");
        this.legacyFilepath = configDir.resolve(name + ".json5");
        this.serializer = serializer;
    }

    @Override
    public void write(T config) throws Exception {
        RawConfigNode current = serializer.serialize(config);
        RawConfigNode previous = config instanceof RawConfigHolder holder ? holder.getRawConfig() : null;
        RawConfigNode merged = previous == null ? current : merge(current, previous);

        Files.createDirectories(filepath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            serializer.write(merged, writer);
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
        RawConfigNode rawData;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            rawData = serializer.parse(reader);
        }
        T result = serializer.deserialize(rawData, configClass);
        if (result instanceof RawConfigHolder holder) {
            holder.setRawConfig(rawData);
        }
        return result;
    }


    /**
     * Recursively preserve unknown fields from unrecognized config versions.
     * <p>
     * Merges keys from {@code previous} unless they are already present in {@code current}.
     * <p>
     * Merges recursively when both sides have a {@link RawConfigNode#isObject() object} at the same key.
     * Non-object fields present in {@code current} are always kept, taking precedence over conflicting fields in
     * {@code previous}.
     *
     * @return a new {@link RawConfigNode}
     */
    private RawConfigNode merge(RawConfigNode current, RawConfigNode previous) {
        RawConfigNode result = serializer.deepCopy(current);
        for (Map.Entry<String, RawConfigNode> entry : previous.entries()) {
            String key = entry.getKey();
            RawConfigNode previousValue = entry.getValue();
            RawConfigNode currentValue = current.get(key);

            if (currentValue == null) {
                result.add(key, serializer.deepCopy(previousValue));
            } else if (currentValue.isObject() && previousValue.isObject()) {
                result.add(key, merge(currentValue, previousValue));
            }
        }
        return result;
    }
}
