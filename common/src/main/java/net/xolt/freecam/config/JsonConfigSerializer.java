package net.xolt.freecam.config;

// FIXME: add our own jankson dependency
// and/or use a different json/config library
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonConfigSerializer<T> implements ConfigSerializer<T> {

    private final Class<T> configClass;
    private final String name;
    private final Path filepath;
    private final Jankson jankson;

    public JsonConfigSerializer(Class<T> configClass, String name) {
        this(configClass, name, Jankson.builder().build());
    }

    public JsonConfigSerializer(Class<T> configClass, String name, Jankson jankson) {
        this.configClass = configClass;
        this.name = name;
        this.jankson = jankson;
        // TODO: consider whether this should be hard-coded or configurable...
        this.filepath = Minecraft.getInstance().gameDirectory
                .toPath()
                .resolve("config")
                .resolve(name + ".json5");
    }

    @Override
    public void serialize(T config) throws Exception {
        Files.createDirectories(filepath.getParent());
        BufferedWriter writer = Files.newBufferedWriter(filepath);
        String json = jankson.toJson(config).toJson(true, true);
        writer.write(json);
        writer.close();
    }

    @Override
    public @Nullable T deserialize() throws Exception {
        // Return an "default" instance if no config file exists
        if (!Files.exists(filepath)) {
            return configClass.getConstructor().newInstance();
        }

        // Deserialize the file to an instance of config class
        InputStream stream = Files.newInputStream(filepath);
        JsonObject json = jankson.load(stream);
        stream.close();
        return jankson.fromJson(json, configClass);
    }
}
