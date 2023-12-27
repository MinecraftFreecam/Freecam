package net.xolt.freecam.tripod;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;

public enum TripodSlot {
    NONE, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

    public static final int MIN = 1;
    public static final int MAX = values().length - 1;

    @Override
    public String toString() {
        return this == NONE ? "None" : "#%d".formatted(ordinal());
    }

    public static boolean inRange(int number) {
        return number >= MIN && number <= MAX;
    }

    public static TripodSlot valueOf(int number) throws IndexOutOfBoundsException {
        if (inRange(number)) {
            throw new IndexOutOfBoundsException("Cannot get TripodSlot for number %d: must be %d-%d.".formatted(number, MIN, MAX));
        }
        return valueOfUnsafe(number);
    }

    public static TripodSlot ofKeyCode(int keyCode) {
        int number = keyCode - GLFW_KEY_0;
        return inRange(number) ? valueOfUnsafe(number) : NONE;
    }

    private static TripodSlot valueOfUnsafe(int number) {
        return values()[number];
    }
}
