package net.xolt.freecam.config.load;

import java.io.Reader;
import java.io.Writer;

public interface ConfigSerializer<N> {
    N parse(Reader reader) throws Exception;
    void write(N node, Writer writer) throws Exception;

    <T> T deserialize(N node, Class<T> configClass);
    <T> N serialize(T config);
}
