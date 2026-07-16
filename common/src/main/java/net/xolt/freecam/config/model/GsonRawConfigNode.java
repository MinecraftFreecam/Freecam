package net.xolt.freecam.config.model;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GsonRawConfigNode implements RawConfigNode {

    private final @NotNull JsonElement json;

    GsonRawConfigNode(@NotNull JsonElement json) {
        this.json = Objects.requireNonNull(json);
    }

    public @NotNull JsonElement getJsonElement() {
        return json;
    }
}
