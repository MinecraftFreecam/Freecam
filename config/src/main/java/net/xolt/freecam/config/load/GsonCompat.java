package net.xolt.freecam.config.load;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class GsonCompat {

    private static final Method PARSE_READER = setupParseReader();
    private static final Method DEEP_COPY = setupDeepCopy();
    private static Gson gsonInstance = null;

    private GsonCompat() {}

    /**
     * Polyfill for Gson &lt;2.8.6 {@link JsonParser#parseReader(Reader)}.
     *
     * @param reader JSON text
     * @return a parse tree of {@link JsonElement}s corresponding to the specified JSON
     * @throws JsonParseException if there is an IOException or if the specified text is not valid JSON
     */
    public static JsonElement parse(Reader reader) throws JsonIOException, JsonSyntaxException {
        if (PARSE_READER != null) {
            try {
                return (JsonElement) PARSE_READER.invoke(null, reader);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke JsonParser.parseReader", e);
            }
        }

        final Gson gson = getGson();
        return gson.fromJson(reader, JsonElement.class);
    }

    /**
     * Polyfill for Gson &lt;2.8.2 {@link JsonElement#deepCopy()}.
     */
    @SuppressWarnings("unchecked")
    public static <T extends JsonElement> T deepCopy(T element) {
        if (DEEP_COPY != null) {
            try {
                return (T) DEEP_COPY.invoke(element);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke JsonElement.deepCopy", e);
            }
        }

        final Gson gson = getGson();
        return (T) gson.fromJson(gson.toJson(element), JsonElement.class);
    }

    private static Gson getGson() {
        if (gsonInstance == null) {
            gsonInstance = new Gson();
        }
        return gsonInstance;
    }

    private static @Nullable Method setupParseReader() {
        try {
            return JsonParser.class.getMethod("parseReader", Reader.class);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static @Nullable Method setupDeepCopy() {
        try {
            return JsonElement.class.getMethod("deepCopy");
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
