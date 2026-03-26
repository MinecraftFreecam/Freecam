package net.xolt.freecam.clothconfig.model;

// FIXME: don't rely on clothconfig's shadowed libs
// NOTE: Minecraft comes with GSON
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import net.minecraft.client.Minecraft;
import net.xolt.freecam.config.model.ConfigLoader;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: Jankson to Jackson-JR or GSON
public class JanksonConfigLoader<T> implements ConfigLoader<T> {

    private final Class<T> configClass;
    private final Path filepath;
    private final Jankson jankson;

    public JanksonConfigLoader(Class<T> configClass, String name) {
        this(configClass, name, Jankson.builder().build());
    }

    public JanksonConfigLoader(Class<T> configClass, String name, Jankson jankson) {
        this.configClass = configClass;
        this.jankson = jankson;
        // TODO: json5 → json (we don't use any json5 features)
        this.filepath = Minecraft.getInstance().gameDirectory
                .toPath()
                .resolve("config")
                .resolve(name + ".json5");
    }

    @Override
    public void write(T config) throws Exception {
        String json = jankson.toJson(config).toJson(true, true);
        Files.createDirectories(filepath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {
            writer.write(json);
        }
    }

    @Override
    public @Nullable T read() throws Exception {
        // Return a "default" instance if no config file exists
        if (!Files.exists(filepath)) {
            return configClass.getConstructor().newInstance();
        }

        // Deserialize the config file
        try (InputStream inputStream = Files.newInputStream(filepath)) {
            JsonObject json = jankson.load(inputStream);
            return jankson.fromJson(json, configClass);
        }
    }
}
