package net.xolt.freecam.config.model;

import java.util.Map;

public interface RawConfigNode {
    boolean isObject();
    RawConfigNode get(String key);
    void add(String key, RawConfigNode value);
    Iterable<Map.Entry<String, RawConfigNode>> entries();
}
