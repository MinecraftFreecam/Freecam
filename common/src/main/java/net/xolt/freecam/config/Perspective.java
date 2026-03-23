package net.xolt.freecam.config;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

public enum Perspective implements SelectionListEntry.Translatable {
    FIRST_PERSON("firstPerson"),
    THIRD_PERSON("thirdPerson"),
    THIRD_PERSON_MIRROR("thirdPersonMirror"),
    INSIDE("inside");

    private final String key;

    Perspective(String name) {
        this.key = "text.autoconfig.freecam.option.visual.perspective." + name;
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }
}
