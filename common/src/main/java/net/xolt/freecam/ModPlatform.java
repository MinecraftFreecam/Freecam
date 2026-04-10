package net.xolt.freecam;

import net.xolt.freecam.util.MultiServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

public interface ModPlatform {

    boolean isModLoaded(String modId);

    static ModPlatform get() {
        return Holder.INSTANCE;
    }

    final class Holder {
        private Holder() {}

        private static final Logger LOGGER = LoggerFactory.getLogger("Freecam/ModPlatform");
        private static final ModPlatform INSTANCE;

        static {
            Collection<Provider<ModPlatform>> providers = MultiServiceLoader.get(ModPlatform.class);

            LOGGER.debug("Loaded: {}", providers.stream()
                    .map(provider -> provider.type().getSimpleName())
                    .collect(Collectors.joining(", "))
            );

            INSTANCE = providers.stream()
                    .findFirst()
                    .map(provider -> {
                        LOGGER.debug("Using {}", provider.type().getSimpleName());
                        return provider.get();
                    })
                    .orElseThrow(() -> {
                        String msg = "No provider found";
                        LOGGER.error(msg);
                        return new IllegalStateException(msg);
                    });
        }
    }
}
