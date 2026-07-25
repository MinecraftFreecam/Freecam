package net.xolt.freecam.config.load;

import java.nio.file.Path;

public interface ConfigLoader<T> {

    void write(T config) throws Exception;
    T read() throws Exception;
    Path getFilepath();
}
