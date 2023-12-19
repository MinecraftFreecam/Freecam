package net.xolt.freecam.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public enum ModBindings {

    KEY_TOGGLE("toggle", GLFW_KEY_F4),
    KEY_PLAYER_CONTROL("playerControl"),
    KEY_TRIPOD_RESET("tripodReset"),
    KEY_CONFIG_GUI("configGui");

    private final Supplier<KeyBinding> lazyBinding;

    ModBindings(String translationKey) {
        this(translationKey, InputUtil.Type.KEYSYM, GLFW_KEY_UNKNOWN);
    }

    ModBindings(String translationKey, int code) {
        this(translationKey, InputUtil.Type.KEYSYM, code);
    }

    ModBindings(String translationKey, InputUtil.Type type) {
        this(translationKey, type, GLFW_KEY_UNKNOWN);
    }

    ModBindings(String translationKey, InputUtil.Type type, int code) {
        this.lazyBinding = Suppliers.memoize(() ->
                new KeyBinding("key.freecam." + translationKey, type, code, "category.freecam.freecam"));
    }

    /**
     * @return the result of calling {@link KeyBinding#isPressed()} on the represented {@link KeyBinding}.
     * @see KeyBinding#isPressed()
     */
    public boolean isPressed() {
        return get().isPressed();
    }

    /**
     * @return the result of calling {@link KeyBinding#wasPressed()} on the represented {@link KeyBinding}.
     * @see KeyBinding#wasPressed()
     */
    public boolean wasPressed() {
        return get().wasPressed();
    }

    /**
     * Lazily get the actual {@link KeyBinding} represented by this enum value.
     * <p>
     * Values are constructed if they haven't been already.
     *
     * @return the actual {@link KeyBinding}.
     */
    public KeyBinding get() {
        return lazyBinding.get();
    }

    /**
     * Calls {@code action} using each {@link KeyBinding} owned by this enum.
     * <p>
     * Values are constructed if they haven't been already.
     * <p>
     * Static implementation of {@link Iterable#forEach(Consumer)}.
     */
    public static void forEach(@NotNull Consumer<KeyBinding> action) {
        Objects.requireNonNull(action);
        iterator().forEachRemaining(action);
    }

    /**
     * Static implementation of {@link Iterable#iterator()}.
     */
    public static @NotNull Iterator<KeyBinding> iterator() {
        return Arrays.stream(values())
                .map(ModBindings::get)
                .iterator();
    }

    /**
     * Static implementation of {@link Iterable#spliterator()}.
     */
    public static @NotNull Spliterator<KeyBinding> spliterator() {
        return Arrays.stream(values())
                .map(ModBindings::get)
                .spliterator();
    }
}
