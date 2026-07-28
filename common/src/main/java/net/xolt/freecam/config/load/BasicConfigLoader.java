package net.xolt.freecam.config.load;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class BasicConfigLoader<T, S> implements ConfigLoader<T> {

    private final ConfigSerializer<S> serializer;
    private final Class<T> configClass;
    private final Path filepath;

    public BasicConfigLoader(ConfigSerializer<S> serializer, Class<T> configClass, Path filepath) {
        this.serializer = serializer;
        this.configClass = configClass;
        this.filepath = filepath;
    }

    @Override
    public T read() throws Exception {
        if (!Files.exists(filepath)) {
            return configClass.getConstructor().newInstance();
        }

        S data;
        try (BufferedReader reader = Files.newBufferedReader(filepath)) {
            data = serializer.parse(reader);
        }
        return serializer.deserialize(data, configClass);
    }

    @Override
    public void write(T config) throws Exception {
        S data = serializer.serialize(config);

        Files.createDirectories(filepath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            serializer.write(data, writer);
        }
    }

    @Override
    public Path getFilepath() {
        return filepath;
    }
}
