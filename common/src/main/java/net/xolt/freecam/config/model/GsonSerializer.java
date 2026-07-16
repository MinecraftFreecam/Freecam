package net.xolt.freecam.config.model;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Collectors;

public class GsonSerializer implements ConfigSerializer<GsonRawConfigNode> {

    private final Gson gson;

    public GsonSerializer() {
        this(new GsonBuilder().setPrettyPrinting().create());
    }

    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public GsonRawConfigNode parse(Reader reader) {
        //~ if gson: >=2.8.6 'new JsonParser().parse' -> 'JsonParser.parseReader'
        JsonObject rawJson = JsonParser.parseReader(reader).getAsJsonObject();
        return new GsonRawConfigNode(rawJson);
    }

    @Override
    public void write(GsonRawConfigNode node, Writer writer) {
        gson.toJson(node.getJsonElement(), writer);
    }

    @Override
    public <T> T deserialize(GsonRawConfigNode node, Class<T> configClass) {
        return gson.fromJson(node.getJsonElement(), configClass);
    }

    @Override
    public <T> GsonRawConfigNode serialize(T config) {
        return new GsonRawConfigNode(gson.toJsonTree(config).getAsJsonObject());
    }

    @Override
    public GsonRawConfigNode deepCopy(GsonRawConfigNode node) {
        JsonElement json = node.getJsonElement();
        //? if gson: >=2.8.2 {
        return new GsonRawConfigNode(json.deepCopy());
        //? } else
        //return new GsonRawConfigNode(gson.fromJson(gson.toJson(json), JsonElement.class));
    }

    @Override
    public boolean isObject(GsonRawConfigNode node) {
        return node.getJsonElement().isJsonObject();
    }

    @Override
    public @Nullable GsonRawConfigNode get(GsonRawConfigNode node, String key) {
        JsonElement element = node.getJsonElement().getAsJsonObject().get(key);
        return element == null ? null : new GsonRawConfigNode(element);
    }

    @Override
    public void add(GsonRawConfigNode node, String key, GsonRawConfigNode value) {
        node.getJsonElement().getAsJsonObject().add(key, value.getJsonElement());
    }

    @Override
    public Iterable<Map.Entry<String, GsonRawConfigNode>> entries(GsonRawConfigNode node) {
        return node.getJsonElement().getAsJsonObject().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), new GsonRawConfigNode(entry.getValue())))
            .collect(Collectors.toUnmodifiableList());
    }

}
