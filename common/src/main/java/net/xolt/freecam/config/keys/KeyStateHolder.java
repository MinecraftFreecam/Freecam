package net.xolt.freecam.config.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

import static net.xolt.freecam.Freecam.MC;

public class KeyStateHolder {

    private static boolean isSneakKeyDown;

    private KeyStateHolder() {}

    /// Effective sneak key state for gameplay purposes, which is different to its
    /// [logical state][net.minecraft.client.ToggleKeyMapping#isDown()]
    /// when 'toggle sneak' is enabled.
    public static boolean isSneakKeyDown() {
        return isSneakKeyDown;
    }

    @ApiStatus.Internal
    public static void update(InputConstants.Key key, boolean state) {
        if (Objects.equals(MC.options.keyShift.key, key)) {
            isSneakKeyDown = state;
        }
    }

    @ApiStatus.Internal
    public static void update(KeyMapping mapping, boolean state) {
        if (mapping == MC.options.keyShift) {
            isSneakKeyDown = state;
        }
    }
}
