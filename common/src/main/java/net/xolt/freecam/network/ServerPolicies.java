package net.xolt.freecam.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public final class ServerPolicies {
    public static final String CHANNEL = "freecam:server_config";
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPolicies.class);
    private static final Policies ALLOW_ALL = new Policies(true, true, true, true);
    private static volatile Policies current = ALLOW_ALL;

    public static boolean allowFreecam() {
        return current.allowFreecam;
    }

    public static boolean allowClipping() {
        return current.allowClipping;
    }

    public static boolean allowFullbright() {
        return current.allowFullbright;
    }

    public static boolean allowInteract() {
        return current.allowInteract;
    }
  
    public static void reset() {
        current = ALLOW_ALL;
    }

    public static boolean applyBytes(byte[] payload) {
        return applyJson(new String(payload, StandardCharsets.UTF_8));
    }

    public static boolean applyJson(String json) {
        try {
            JsonElement rootElement = parse(json);
            if (!rootElement.isJsonObject()) {
                throw new JsonParseException("root must be a JSON object");
            }

            JsonObject root = rootElement.getAsJsonObject();
            Policies parsed = new Policies(
                    readBoolean(root, "allowFreecam"),
                    readBoolean(root, "allowClipping"),
                    readBoolean(root, "allowFullbright"),
                    readBoolean(root, "allowInteract")
            );
            current = parsed;
            LOGGER.debug("Applied server Freecam policies: {}", parsed);
            return true;
        } catch (JsonParseException | IllegalStateException | ClassCastException e) {
            LOGGER.warn("Ignoring invalid {} payload: {}", CHANNEL, e.getMessage());
            return false;
        }
    }

    private static JsonElement parse(String json) {
        return new JsonParser().parse(json);
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

    private static final class Policies {
        private final boolean allowFreecam;
        private final boolean allowClipping;
        private final boolean allowFullbright;
        private final boolean allowInteract;

        private Policies(boolean allowFreecam, boolean allowClipping, boolean allowFullbright, boolean allowInteract) {
            this.allowFreecam = allowFreecam;
            this.allowClipping = allowClipping;
            this.allowFullbright = allowFullbright;
            this.allowInteract = allowInteract;
        }

        @Override
        public String toString() {
            return "Policies{" +
                    "allowFreecam=" + allowFreecam +
                    ", allowClipping=" + allowClipping +
                    ", allowFullbright=" + allowFullbright +
                    ", allowInteract=" + allowInteract +
                    '}';
        }
    }
}
