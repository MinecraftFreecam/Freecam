package net.xolt.freecam.accessors;

import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

/// Internal mixin accessor for [KeyMapping].
@ApiStatus.Internal
public interface KeyMappingInternalAccessor {
    void freecam$setKeyDown(boolean state);
}
