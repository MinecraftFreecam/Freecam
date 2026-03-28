package net.xolt.freecam.config.model;

public interface ConfigController<T> {
    /** Returns the currently loaded config, or defaults if not yet loaded. */
    T getConfig();

    /** Returns the default values. */
    T getDefaults();

    /** Load or reload from disk. */
    void load();

    /** Save current config to disk. */
    void save();
}
