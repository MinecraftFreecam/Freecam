package net.xolt.freecam.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/** Connection-scoped overrides; never serialized into the local configuration. */
public final class ServerPolicies {
    public static final String CHANNEL = "freecam:server_config";
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPolicies.class);
    private static final TypeAdapter<JsonElement> JSON_ADAPTER = new Gson().getAdapter(JsonElement.class);
    private static final ServerPolicy ALLOW_ALL = ServerPolicy.ALLOW_ALL;
    private static volatile ServerPolicy current = ALLOW_ALL;
    private static volatile boolean forceCollision;
    private static volatile ServerPolicy hostPolicy;
    public static final String ANTI_FREECAM_CHANNEL = "antifreecam:freecam_config_packet";

    private ServerPolicies() {}

    public static boolean allowFreecam() {
        return effective().allowFreecam();
    }

    public static boolean allowIgnoringCollision() {
        ServerPolicy host = hostPolicy;
        if (host != null) {
            return host.collision().allowIgnoring();
        }
        return !forceCollision && current.collision().allowIgnoring();
    }

    public static boolean allowFullbright() {
        return effective().allowFullbright();
    }

    public static boolean allowInteract() {
        return effective().allowInteract();
    }

    /** While hosting, the local config decides instead of received policies; {@code null} when not hosting. */
    public static void setHostPolicy(@Nullable ServerPolicy policy) {
        hostPolicy = policy;
    }

    public static void reset() {
        current = ALLOW_ALL;
        forceCollision = false;
        hostPolicy = null;
    }

    private static ServerPolicy effective() {
        ServerPolicy host = hostPolicy;
        return host != null ? host : current;
    }

    public static void applyAntiFreecam(boolean force) {
        forceCollision = force;
    }

    public static boolean applyAntiFreecamBytes(byte[] payload) {
        if (payload.length != 1) {
            LOGGER.warn("Ignoring invalid {} payload length: {}", ANTI_FREECAM_CHANNEL, payload.length);
            return false;
        }
        applyAntiFreecam(payload[0] != 0);
        return true;
    }

    public static boolean applyBytes(byte[] payload) {
        return applyJson(new String(payload, StandardCharsets.UTF_8));
    }

    /** Replaces the policy atomically. Missing fields allow the feature; invalid updates preserve it. */
    public static boolean applyJson(String json) {
        try {
            JsonElement rootElement = parse(json);
            if (!rootElement.isJsonObject()) {
                throw new JsonParseException("root must be a JSON object");
            }

            JsonObject root = rootElement.getAsJsonObject();
            JsonObject collision = readObject(root, "collision");
            ServerPolicy parsed = new ServerPolicy(
                    readBoolean(root, "allowFreecam"),
                    new ServerPolicy.CollisionPolicy(readBoolean(collision, "allowIgnoring")),
                    readBoolean(root, "allowFullbright"),
                    readBoolean(root, "allowInteract")
            );
            current = parsed;
            LOGGER.debug("Applied server Freecam policies: {}", parsed);
            return true;
        } catch (IOException | JsonParseException | IllegalStateException e) {
            LOGGER.warn("Ignoring invalid {} payload: {}", CHANNEL, e.getMessage());
            return false;
        }
    }

    private static JsonElement parse(String json) throws IOException {
        // JsonParser enables lenient parsing; read through the adapter to require JSON syntax.
        try (JsonReader reader = new JsonReader(new StringReader(json))) {
            JsonElement root = JSON_ADAPTER.read(reader);
            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonParseException("unexpected data after policy");
            }
            return root;
        }
    }

    private static JsonObject readObject(JsonObject root, String key) {
        JsonElement value = root.get(key);
        if (value == null) {
            return new JsonObject();
        }
        if (!value.isJsonObject()) {
            throw new JsonParseException("'" + key + "' must be an object");
        }
        return value.getAsJsonObject();
    }

    private static boolean readBoolean(JsonObject root, String key) {
        JsonElement value = root.get(key);
        if (value == null) {
            return true;
        }
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
            throw new JsonParseException("'" + key + "' must be a boolean");
        }
        return value.getAsBoolean();
    }

}
