package net.xolt.freecam.config.model;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.stream.Collectors;

public class GsonRawConfigNode implements RawConfigNode {

    private final JsonElement json;

    GsonRawConfigNode(JsonElement json) {
        this.json = json;
    }

    @Override
    public boolean isObject() {
        return json.isJsonObject();
    }

    @Override
    public RawConfigNode get(String key) {
        JsonElement element = json.getAsJsonObject().get(key);
        return new GsonRawConfigNode(element);
    }

    @Override
    public void add(String key, RawConfigNode value) {
        if (value instanceof GsonRawConfigNode node) {
            node.json.getAsJsonObject().add(key, node.json);
        } else {
            throw new IllegalStateException("Incompatible config node type");
        }
    }

    @Override
    public Iterable<Map.Entry<String, RawConfigNode>> entries() {
        return json.getAsJsonObject().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), (RawConfigNode) new GsonRawConfigNode(entry.getValue())))
            .collect(Collectors.toUnmodifiableList());
    }

    public JsonElement getJsonElement() {
        return json;
    }
}
