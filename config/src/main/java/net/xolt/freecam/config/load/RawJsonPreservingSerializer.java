package net.xolt.freecam.config.load;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.io.Writer;
import java.util.Map.Entry;

public class RawJsonPreservingSerializer implements ConfigSerializer<JsonElement> {

    private final ConfigSerializer<JsonElement> delegate;

    public RawJsonPreservingSerializer() {
        this(new GsonSerializer());
    }

    public RawJsonPreservingSerializer(ConfigSerializer<JsonElement> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T deserialize(JsonElement json, Class<T> configClass) {
        T config = delegate.deserialize(json, configClass);
        if (config instanceof RawJsonHolder holder && json.isJsonObject()) {
            holder.setRawJson(json.getAsJsonObject());
        }
        return config;
    }

    @Override
    public <T> JsonElement serialize(T config) {
        JsonElement current = delegate.serialize(config);
        if (config instanceof RawJsonHolder holder) {
            JsonObject previous = holder.getRawJson();
            if (previous != null) return merge(current, previous);
        }
        return current;
    }

    @Override
    public JsonElement parse(Reader reader) throws Exception {
        return delegate.parse(reader);
    }

    @Override
    public void write(JsonElement json, Writer writer) throws Exception {
        delegate.write(json, writer);
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
     * @return a new {@link JsonElement}, or the original if unchanged.
     */
    JsonElement merge(JsonElement current, JsonElement previous) {
        if (current == null) return previous;
        if (previous == null) return current;

        if (current.isJsonObject() && previous.isJsonObject()) {
            JsonObject result = GsonCompat.deepCopy(current).getAsJsonObject();
            for (Entry<String, JsonElement> entry : previous.getAsJsonObject().entrySet()) {
                String key = entry.getKey();
                JsonElement previousValue = entry.getValue();
                JsonElement currentValue = result.get(key);

                // If current doesn't have it, or both are objects, merge recursively
                if (currentValue == null || (currentValue.isJsonObject() && previousValue.isJsonObject())) {
                    result.add(key, merge(currentValue, previousValue));
                }
            }
            return result;
        }

        // Otherwise: current overwrites previous value
        return current;
    }
}
