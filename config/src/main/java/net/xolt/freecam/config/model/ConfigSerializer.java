package net.xolt.freecam.config.model;

import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public interface ConfigSerializer<N extends RawConfigNode> {
    N parse(Reader reader) throws Exception;
    void write(N node, Writer writer) throws Exception;

    <T> T deserialize(N node, Class<T> configClass);
    <T> N serialize(T config);

    N deepCopy(N node);
    boolean isObject(N node);
    @Nullable N get(N node, String key);
    void add(N node, String key, N value);
    Iterable<Map.Entry<String, N>> entries(N node);
}
