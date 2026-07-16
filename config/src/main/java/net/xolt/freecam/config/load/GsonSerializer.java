package net.xolt.freecam.config.load;

import com.google.gson.*;

import java.io.Reader;
import java.io.Writer;

public class GsonSerializer implements ConfigSerializer<JsonElement> {

    private final Gson gson;

    public GsonSerializer() {
        this(new GsonBuilder().setPrettyPrinting().create());
    }

    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public JsonElement parse(Reader reader) throws JsonIOException, JsonSyntaxException {
        return GsonCompat.parse(reader);
    }

    @Override
    public void write(JsonElement json, Writer writer) throws JsonIOException {
        gson.toJson(json, writer);
    }

    @Override
    public <T> T deserialize(JsonElement json, Class<T> configClass) {
        return gson.fromJson(json, configClass);
    }


    @Override
    public <T> JsonObject serialize(T config) {
        return gson.toJsonTree(config).getAsJsonObject();
    }
}
