package net.xolt.freecam.config;

import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.util.OptionalService;
import net.xolt.freecam.util.OptionalServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static net.xolt.freecam.Freecam.MC;

public interface ConfigScreenProvider extends OptionalService {

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
        private static final Optional<ConfigScreenProvider> INSTANCE = OptionalServiceLoader.get(ConfigScreenProvider.class);

        static {
            INSTANCE.ifPresentOrElse(
                    service -> LOGGER.info("Using {}", service.getName()),
                    () -> LOGGER.info("No config screen provider available — GUI disabled")
            );
        }
    }
}
