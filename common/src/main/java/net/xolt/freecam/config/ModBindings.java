package net.xolt.freecam.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public enum ModBindings {

    KEY_TOGGLE("toggle", GLFW_KEY_F4),
    KEY_PLAYER_CONTROL("playerControl"),
    KEY_TRIPOD_RESET("tripodReset"),
    KEY_CONFIG_GUI("configGui");

    private final Supplier<KeyMapping> lazyBinding;

    ModBindings(String translationKey) {
        this(translationKey, InputConstants.Type.KEYSYM, GLFW_KEY_UNKNOWN);
    }

    ModBindings(String translationKey, int code) {
        this(translationKey, InputConstants.Type.KEYSYM, code);
    }

    ModBindings(String translationKey, InputConstants.Type type) {
        this(translationKey, type, GLFW_KEY_UNKNOWN);
    }

    ModBindings(String translationKey, InputConstants.Type type, int code) {
        this.lazyBinding = Suppliers.memoize(() ->
                new KeyMapping("key.freecam." + translationKey, type, code, "category.freecam.freecam"));
    }

    /**
     * @return the result of calling {@link KeyMapping#isDown()} on the represented {@link KeyMapping}.
     * @see KeyMapping#isDown()
     */
    public boolean isPressed() {
        return get().isDown();
    }

    /**
     * @return the result of calling {@link KeyMapping#consumeClick()} on the represented {@link KeyMapping}.
     * @see KeyMapping#consumeClick()
     */
    public boolean wasPressed() {
        return get().consumeClick();
    }

    /**
     * Lazily get the actual {@link KeyMapping} represented by this enum value.
     * <p>
     * Values are constructed if they haven't been already.
     *
     * @return the actual {@link KeyMapping}.
     */
    public KeyMapping get() {
        return lazyBinding.get();
    }

    /**
     * Calls {@code action} using each {@link KeyMapping} owned by this enum.
     * <p>
     * Values are constructed if they haven't been already.
     * <p>
     * Static implementation of {@link Iterable#forEach(Consumer)}.
     */
    public static void forEach(@NotNull Consumer<KeyMapping> action) {
        Objects.requireNonNull(action);
        iterator().forEachRemaining(action);
    }

    /**
     * Static implementation of {@link Iterable#iterator()}.
     */
    public static @NotNull Iterator<KeyMapping> iterator() {
        return Arrays.stream(values())
                .map(ModBindings::get)
                .iterator();
    }

    /**
     * Static implementation of {@link Iterable#spliterator()}.
     */
    public static @NotNull Spliterator<KeyMapping> spliterator() {
        return Arrays.stream(values())
                .map(ModBindings::get)
                .spliterator();
    }
}
