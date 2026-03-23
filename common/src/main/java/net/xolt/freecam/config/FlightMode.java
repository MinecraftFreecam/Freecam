package net.xolt.freecam.config;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

public enum FlightMode implements SelectionListEntry.Translatable {
    CREATIVE("creative"),
    DEFAULT("default");

    private final String key;

    FlightMode(String name) {
        this.key = "text.autoconfig.freecam.option.movement.flightMode." + name;
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }
}
