package net.xolt.freecam.config.load;

import net.xolt.freecam.config.model.ModConfigDTO;

import java.nio.file.Path;

public class ModConfigLoader extends MigratingConfigLoader<ModConfigDTO> {

    public ModConfigLoader(ConfigSerializer<?> serializer, String name, Path configDir) {
        super(
            new BasicConfigLoader<>(serializer, ModConfigDTO.class, configDir.resolve(name + ".json")),
            new BasicConfigLoader<>(serializer, ModConfigDTO.class, configDir.resolve(name + ".json5"))
        );
    }
}
