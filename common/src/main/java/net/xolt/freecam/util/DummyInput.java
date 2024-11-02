package net.xolt.freecam.util;

import net.minecraft.client.player.Input;

public class DummyInput extends Input {

    public DummyInput(Input old) {
        this(old.shiftKeyDown);
    }

    public DummyInput(boolean isSneaking) {
        // Makes player continue to sneak after freecam is enabled.
        this.shiftKeyDown = isSneaking;
    }
}
