package net.xolt.freecam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.currentThread;

public class OptionalServiceLoader<T extends OptionalService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalServiceLoader.class);
    private static final Map<Class<?>, OptionalServiceLoader<?>> SERVICE_LOADERS = new ConcurrentHashMap<>();

    private final Class<T> serviceClass;
    private final Map<ClassLoader, Optional<T>> serviceInstances = new ConcurrentHashMap<>();

    private OptionalServiceLoader(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public static <T extends OptionalService> Optional<T> get(Class<T> serviceClass) {
        return getServiceLoader(serviceClass).getService();
    }

    @SuppressWarnings("unchecked")
    private static <T extends OptionalService> OptionalServiceLoader<T> getServiceLoader(Class<T> serviceClass) {
        return (OptionalServiceLoader<T>) SERVICE_LOADERS.computeIfAbsent(serviceClass, key -> new OptionalServiceLoader<>(serviceClass));
    }

    private Optional<T> getService() {
        return serviceInstances.computeIfAbsent(currentThread().getContextClassLoader(), this::loadService);
    }

    private Optional<T> loadService(ClassLoader classLoader) {
        final String name = serviceClass.getSimpleName();
        LOGGER.debug("Loading service providers for {}", name);

        Optional<T> result = ServiceLoader.load(serviceClass, classLoader).stream()
                .map(provider -> {
                    try {
                        return provider.get();
                    } catch (Throwable t) {
                        LOGGER.error("{} provider {} failed to load", name, provider.type().getSimpleName(), t);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(service -> {
                    try {
                        return service.isAvailable();
                    } catch (Throwable t) {
                        LOGGER.error("{} provider {} threw during isAvailable()", name, service.getClass().getName(), t);
                        return false;
                    }
                })
                .findFirst();

        result.ifPresentOrElse(
                service -> LOGGER.debug("Loaded {} service for {}", service.getClass().getSimpleName(), name),
                () -> LOGGER.debug("No service is available for {}", name)
        );

        return result;
    }
}
