package net.xolt.freecam.accessors;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;

/// Mixin accessor for [KeyMapping].
public interface KeyMappingStateAccessor {
    /// Effective key state for gameplay purposes, which may be different to its
    /// [logical state][KeyMapping#isDown()] if this is a [ToggleKeyMapping].
    boolean freecam$isKeyDown();
}
