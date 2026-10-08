package net.xolt.freecam.config.model;

import net.minecraft.network.chat.Component;

/** A feature enabled in the local config that the current server's policy blocks. */
public enum ServerRestrictedFeature {
    IGNORING_COLLISION("ignoringCollision"),
    FULL_BRIGHTNESS("fullBrightness"),
    CAMERA_INTERACTIONS("cameraInteractions");

    private final String key;

    ServerRestrictedFeature(String key) {
        this.key = key;
    }

    public Component getName() {
        return Component.translatable("freecam.msg.restricted.feature." + key);
    }
}
