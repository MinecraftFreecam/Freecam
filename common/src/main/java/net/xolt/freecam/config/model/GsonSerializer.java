package net.xolt.freecam.config.model;

import com.google.gson.*;

import java.io.Reader;
import java.io.Writer;

public class GsonSerializer implements ConfigSerializer {

    private final Gson gson;

    public GsonSerializer() {
        this(new GsonBuilder().setPrettyPrinting().create());
    }

    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public RawConfigNode parse(Reader reader) {
        //~ if gson: >=2.8.6 'new JsonParser().parse' -> 'JsonParser.parseReader'
        JsonObject rawJson = JsonParser.parseReader(reader).getAsJsonObject();
        return new GsonRawConfigNode(rawJson);
    }

    @Override
    public void write(RawConfigNode node, Writer writer) {
        if (!(node instanceof GsonRawConfigNode gsonNode)) {
            throw new IllegalArgumentException("Incompatible config node type");
        }
        gson.toJson(gsonNode.getJsonElement(), writer);
    }

    @Override
    public <T> T deserialize(RawConfigNode node, Class<T> configClass) {
        if (!(node instanceof GsonRawConfigNode gsonNode)) {
            throw new IllegalArgumentException("Incompatible config node type");
        }
        return gson.fromJson(gsonNode.getJsonElement(), configClass);
    }

    @Override
    public <T> RawConfigNode serialize(T config) {
        return new GsonRawConfigNode(gson.toJsonTree(config).getAsJsonObject());
    }

    @Override
    public RawConfigNode deepCopy(RawConfigNode node) {
        if (!(node instanceof GsonRawConfigNode gsonNode)) {
            throw new IllegalStateException("Incompatible config node type");
        }
        JsonElement json = gsonNode.getJsonElement();
        //? if gson: >=2.8.2 {
        return new GsonRawConfigNode(json.deepCopy());
        //? } else
        //return new GsonRawConfigNode(gson.fromJson(gson.toJson(json), JsonElement.class));
    }
}
