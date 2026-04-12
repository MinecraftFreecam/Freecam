package net.xolt.freecam.config.gui;

import java.util.Collections;
import java.util.List;

public /*? if java: >=17 >>*/sealed interface DependencyDescription {
    record Literal(String text) implements DependencyDescription {}
    record Translatable(String key, List<?> args) implements DependencyDescription {
        public Translatable(String key) {
            this(key, Collections.emptyList());
        }
    }
}
