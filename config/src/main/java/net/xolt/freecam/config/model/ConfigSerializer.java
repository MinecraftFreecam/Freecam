package net.xolt.freecam.config.model;

import java.io.Reader;
import java.io.Writer;

public interface ConfigSerializer {
    RawConfigNode parse(Reader reader) throws Exception;
    void write(RawConfigNode node, Writer writer) throws Exception;

    <T> T deserialize(RawConfigNode node, Class<T> configClass);
    <T> RawConfigNode serialize(T config);

    RawConfigNode deepCopy(RawConfigNode node);
}
