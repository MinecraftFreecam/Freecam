package net.xolt.freecam.tripod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.xolt.freecam.util.FreecamPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Persists tripod positions to disk so that they survive game restarts.
 */
public class TripodStorage {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type STORAGE_TYPE = new TypeToken<Map<String, Map<String, Map<String, StoredPosition>>>>() {}.getType();

    private final Path file;
    private final Map<String, Map<String, Map<String, StoredPosition>>> cache = new HashMap<>();

    public TripodStorage() {
        Path configDir = Minecraft.getInstance().gameDirectory.toPath().resolve("config");
        this.file = configDir.resolve("freecam_tripods.json");
        loadFile();
    }

    public Map<ResourceKey<Level>, Map<TripodSlot, FreecamPosition>> load(String scope) {
        Map<ResourceKey<Level>, Map<TripodSlot, FreecamPosition>> result = new HashMap<>();
        Map<String, Map<String, StoredPosition>> scoped = cache.get(scope);
        if (scoped == null) {
            return result;
        }

        scoped.forEach((dimensionId, slots) -> {
            ResourceLocation location = ResourceLocation.tryParse(dimensionId);
            if (location == null) {
                LOGGER.warn("Skipping tripod data with invalid dimension id {}", dimensionId);
                return;
            }
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, location);
            Map<TripodSlot, FreecamPosition> tripodPositions = new EnumMap<>(TripodSlot.class);
            slots.forEach((slotName, storedPosition) -> {
                if (storedPosition == null) {
                    return;
                }
                try {
                    TripodSlot slot = TripodSlot.valueOf(slotName);
                    if (slot != TripodSlot.NONE) {
                        tripodPositions.put(slot, storedPosition.toFreecamPosition());
                    }
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Skipping unknown tripod slot {} in {}", slotName, dimensionId);
                }
            });
            if (!tripodPositions.isEmpty()) {
                result.put(dimension, tripodPositions);
            }
        });
        return result;
    }

    public void save(String scope, Map<ResourceKey<Level>, Map<TripodSlot, FreecamPosition>> tripods) {
        if (scope == null) {
            return;
        }

        if (tripods == null || tripods.isEmpty()) {
            cache.remove(scope);
        } else {
            cache.put(scope, serialize(tripods));
        }
        writeFile();
    }

    private Map<String, Map<String, StoredPosition>> serialize(Map<ResourceKey<Level>, Map<TripodSlot, FreecamPosition>> tripods) {
        Map<String, Map<String, StoredPosition>> serialized = new HashMap<>();
        tripods.forEach((dimension, slots) -> {
            Map<String, StoredPosition> slotMap = new HashMap<>();
            slots.forEach((slot, position) -> {
                if (slot == TripodSlot.NONE || position == null) {
                    return;
                }
                slotMap.put(slot.name(), StoredPosition.from(position));
            });
            if (!slotMap.isEmpty()) {
                serialized.put(dimension.location().toString(), slotMap);
            }
        });
        return serialized;
    }

    private void loadFile() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) {
                return;
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                Map<String, Map<String, Map<String, StoredPosition>>> data = GSON.fromJson(reader, STORAGE_TYPE);
                cache.clear();
                if (data != null) {
                    cache.putAll(data);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read tripod storage file {}", file, e);
        }
    }

    private void writeFile() {
        try {
            Files.createDirectories(file.getParent());
            if (cache.isEmpty()) {
                Files.deleteIfExists(file);
                return;
            }
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(cache, STORAGE_TYPE, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write tripod storage file {}", file, e);
        }
    }

    private static class StoredPosition {
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        StoredPosition() {}

        StoredPosition(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        static StoredPosition from(FreecamPosition position) {
            return new StoredPosition(position.x, position.y, position.z, position.yaw, position.pitch);
        }

        FreecamPosition toFreecamPosition() {
            return new FreecamPosition(x, y, z, yaw, pitch);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StoredPosition that)) return false;
            return Double.compare(that.x, x) == 0
                    && Double.compare(that.y, y) == 0
                    && Double.compare(that.z, z) == 0
                    && Float.compare(that.yaw, yaw) == 0
                    && Float.compare(that.pitch, pitch) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, yaw, pitch);
        }
    }
}
