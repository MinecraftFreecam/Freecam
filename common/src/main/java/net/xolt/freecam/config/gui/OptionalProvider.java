package net.xolt.freecam.config.gui;

public interface OptionalProvider {
    boolean isAvailable();
    DependencyDescription getRequirement();
}
