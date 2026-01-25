package net.xolt.freecam.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import me.shedaniel.autoconfig.AutoConfig;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.keys.FreecamKeyMapping;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.xolt.freecam.Freecam.MC;
import static net.xolt.freecam.config.keys.FreecamKeyMappingBuilder.builder;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;

public enum ModBindings {

    KEY_TOGGLE(() -> builder("toggle")
            .action(Freecam::toggle)
            .holdAction(Freecam::activateTripodHandler)
            .defaultKey(GLFW_KEY_F4)
            .build()),
    KEY_PLAYER_CONTROL(() -> builder("playerControl")
            .action(Freecam::switchControls)
            .build()),
    KEY_TRIPOD_RESET(() -> builder("tripodReset")
            .holdAction(Freecam::resetTripodHandler)
            .build()),
    KEY_CONFIG_GUI(() -> builder("configGui")
            .action(() -> MC.setScreen(ModConfig.getConfigScreen()))
            .build());

    private final Supplier<FreecamKeyMapping> lazyMapping;

    ModBindings(Supplier<FreecamKeyMapping> mappingSupplier) {
        lazyMapping = Suppliers.memoize(mappingSupplier);
    }

    /**
     * Lazily get the actual {@link FreecamKeyMapping} represented by this enum value.
     * <p>
     * Values are constructed if they haven't been already.
     *
     * @return the actual {@link FreecamKeyMapping}.
     */
    public FreecamKeyMapping get() {
        return lazyMapping.get();
    }

    /**
     * Calls {@code action} using each {@link FreecamKeyMapping} owned by this enum.
     * <p>
     * Values are constructed if they haven't been already.
     * <p>
     * Static implementation of {@link Iterable#forEach(Consumer)}.
     */
    public static void forEach(@NotNull Consumer<FreecamKeyMapping> action) {
        Objects.requireNonNull(action);
        iterator().forEachRemaining(action);
    }

    /**
     * Static implementation of {@link Iterable#iterator()}.
     */
    public static @NotNull Iterator<FreecamKeyMapping> iterator() {
        return stream().iterator();
    }

    /**
     * Static implementation of {@link Iterable#spliterator()}.
     */
    public static @NotNull Spliterator<FreecamKeyMapping> spliterator() {
        return stream().spliterator();
    }

    /**
     * Static implementation of {@link Collection#stream()}.
     */
    public static @NotNull Stream<FreecamKeyMapping> stream() {
        return Arrays.stream(values()).map(ModBindings::get);
    }
}
