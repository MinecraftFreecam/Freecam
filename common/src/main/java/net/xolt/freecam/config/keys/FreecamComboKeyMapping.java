package net.xolt.freecam.config.keys;

import com.mojang.blaze3d.platform.InputConstants;


class FreecamComboKeyMapping extends FreecamKeyMapping {

    private final Runnable action;
    private final HoldAction holdAction;
    private final long maxHoldTicks;

    private boolean usedWhileHeld = false;
    private long ticksHeld = 0;

    FreecamComboKeyMapping(String translationKey, InputConstants.Type type, int code, Runnable action, HoldAction holdAction, long maxHoldTicks) {
        super(translationKey, type, code);
        this.action = action;
        this.holdAction = holdAction;
        this.maxHoldTicks = maxHoldTicks;
    }

    @Override
    public void tick() {
        if (isDown()) {
            // Count held ticks, so we can run action on release
            ticksHeld++;
            reset();

            // Handle combo actions
            if (holdAction.run()) {
                usedWhileHeld = true;
            }
        }
        // Check if pressed, but now released
        else if (consumeClick() || ticksHeld > 0) {
            // Only run action if the key wasn't used (or held too long)
            if (!usedWhileHeld && ticksHeld < maxHoldTicks) {
                action.run();
            }
            // Reset state
            reset();
            ticksHeld = 0;
            usedWhileHeld = false;
        }
    }
}
