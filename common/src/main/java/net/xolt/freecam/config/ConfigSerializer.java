package net.xolt.freecam.config;

public interface ConfigSerializer<T> {

    void serialize(T config) throws Exception;

    T deserialize() throws Exception;
}
