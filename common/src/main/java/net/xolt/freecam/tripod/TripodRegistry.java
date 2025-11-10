package net.xolt.freecam.tripod;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.xolt.freecam.util.FreecamPosition;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static net.xolt.freecam.Freecam.MC;

public class TripodRegistry {
    private final Map<ResourceKey<Level>, Map<TripodSlot, FreecamPosition>> tripods = new HashMap<>();
    private final TripodStorage storage = new TripodStorage();
    private String scope;

    public @Nullable FreecamPosition get(TripodSlot tripod) {
        if (MC.level == null) {
            return null;
        }
        ensureScope();
        return get(dimension(), tripod);
    }

    public @Nullable FreecamPosition get(ResourceKey<Level> dimension, TripodSlot tripod) {
        return Optional.ofNullable(tripods.get(dimension))
                .map(positions -> positions.get(tripod))
                .orElse(null);
    }

    public void put(TripodSlot tripod, @Nullable FreecamPosition position) {
        if (MC.level == null) {
            return;
        }
        put(dimension(), tripod, position);
    }

    public void put(ResourceKey<Level> dimension, TripodSlot tripod, @Nullable FreecamPosition position) {
        ensureScope();

        if (position == null) {
            Map<TripodSlot, FreecamPosition> positions = tripods.get(dimension);
            if (positions != null) {
                positions.remove(tripod);
                if (positions.isEmpty()) {
                    tripods.remove(dimension);
                }
                storage.save(scope, tripods);
            }
            return;
        }

        Map<TripodSlot, FreecamPosition> positions = tripods.computeIfAbsent(dimension, key -> new EnumMap<>(TripodSlot.class));
        positions.put(tripod, position);
        storage.save(scope, tripods);
    }

    /**
     * Persist current tripods and drop them from memory.
     */
    public void clear() {
        flush();
        tripods.clear();
        scope = null;
    }

    private void ensureScope() {
        if (MC.level == null) {
            return;
        }
        String currentScope = scopeId();
        if (Objects.equals(currentScope, scope)) {
            return;
        }
        flush();
        tripods.clear();
        scope = currentScope;
        tripods.putAll(storage.load(scope));
    }

    private void flush() {
        if (scope != null) {
            storage.save(scope, tripods);
        }
    }

    private static ResourceKey<Level> dimension() {
        return MC.level.dimension();
    }

    private static String scopeId() {
        ServerData server = MC.getCurrentServer();
        if (server != null) {
            return "server:" + server.ip.trim().toLowerCase(Locale.ROOT);
        }
        if (MC.hasSingleplayerServer()) {
            return "singleplayer";
        }
        return "global";
    }
}
