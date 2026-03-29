package net.xolt.freecam.config.gui;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.util.MultiServiceLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import static net.xolt.freecam.Freecam.MC;

public interface ConfigScreenProvider extends OptionalProvider {

    String getName();

    Screen getConfigScreen(Screen parent);

    default void openConfigScreen() {
        openConfigScreen(MC.screen);
    }

    default void openConfigScreen(Screen parent) {
        MC.setScreen(getConfigScreen(parent));
    }

    static Optional<ConfigScreenProvider> provider() {
        return Holder.INSTANCE;
    }

    final class Holder {
        private Holder() {}

        private static final Logger LOGGER = LoggerFactory.getLogger(ConfigScreenProvider.class);
        private static final Optional<ConfigScreenProvider> INSTANCE;

        static {
            // ConfigScreenProviders that are on the classpath.
            // I.e., implementations supported by the current Freecam mod or modpack.
            Collection<Provider<ConfigScreenProvider>> providers = MultiServiceLoader.get(ConfigScreenProvider.class);

            LOGGER.debug("Loaded: {}", providers.stream()
                    .map(provider -> provider.type().getSimpleName())
                    .collect(Collectors.joining(", "))
            );

            INSTANCE = providers.stream()
                    .map(Holder::attemptLoad)
                    .filter(Objects::nonNull)
                    .filter(OptionalProvider::isAvailable)
                    .findFirst();

            INSTANCE.ifPresentOrElse(
                    service -> LOGGER.info("Using {}", service.getName()),
                    () -> LOGGER.info("No config screen provider available — GUI disabled")
            );
        }

        private static @Nullable ConfigScreenProvider attemptLoad(Provider<ConfigScreenProvider> provider) {
            try {
                return provider.get();
            } catch (Throwable t) {
                LOGGER.error("{} provider {} failed to load", ConfigScreenProvider.class.getSimpleName(), provider.type().getSimpleName(), t);
                return null;
            }
        }
    }
}
