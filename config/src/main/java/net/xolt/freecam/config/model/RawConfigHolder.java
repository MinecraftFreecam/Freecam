package net.xolt.freecam.config.model;

import org.jetbrains.annotations.Nullable;

public interface RawConfigHolder {
    void setRawConfig(RawConfigNode rawJson);
    @Nullable RawConfigNode getRawConfig();
}
