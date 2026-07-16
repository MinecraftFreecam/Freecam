package net.xolt.freecam.config.model;

public interface ConfigLoader<T> {

    void write(T config) throws Exception;

    T read() throws Exception;
}
