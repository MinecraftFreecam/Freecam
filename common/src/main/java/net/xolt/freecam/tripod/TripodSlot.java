package net.xolt.freecam.tripod;

import com.mojang.blaze3d.platform.InputConstants;

public enum TripodSlot {
    NONE, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

    public static final int MIN = 1;
    public static final int MAX = values().length - 1;

    @Override
    public String toString() {
        return this == NONE ? "None" : "#%d".formatted(ordinal());
    }

    public static TripodSlot valueOf(int number) throws IndexOutOfBoundsException {
        if (number >= MIN && number <= MAX) return values()[number];
        throw new IndexOutOfBoundsException("Cannot get TripodSlot for number %d: must be %d-%d.".formatted(number, MIN, MAX));
    }

    public static TripodSlot ofKeyCode(int keyCode) {
        // We could compute keys 1-9 from a keycode offset, however an explicit mapping is more maintainable
        return switch (keyCode) {
            case InputConstants.KEY_1 -> ONE;
            case InputConstants.KEY_2 -> TWO;
            case InputConstants.KEY_3 -> THREE;
            case InputConstants.KEY_4 -> FOUR;
            case InputConstants.KEY_5 -> FIVE;
            case InputConstants.KEY_6 -> SIX;
            case InputConstants.KEY_7 -> SEVEN;
            case InputConstants.KEY_8 -> EIGHT;
            case InputConstants.KEY_9 -> NINE;
            default -> NONE;
        };
    }
}
