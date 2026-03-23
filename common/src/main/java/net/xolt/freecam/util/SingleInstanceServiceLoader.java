package net.xolt.freecam.util;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SingleInstanceServiceLoader {

    private static final Map<CacheKey<?>, ServiceLoader.Provider<?>> SERVICE_PROVIDERS = new ConcurrentHashMap<>();

    private static <T> ServiceLoader.Provider<T> compute(CacheKey<T> key) {
        List<ServiceLoader.Provider<T>> providers = ServiceLoader.load(key.spi, key.classLoader).stream().toList();

        if (providers.isEmpty()) {
            String message = String.format("Could not find any service providers for %s", key.spi.getSimpleName());
            throw new IllegalStateException(message);
        }

        if (providers.size() > 1) {
            String names = providers.stream()
                    .map(provider -> provider.type().getSimpleName())
                    .map(name -> "\n - " + name)
                    .collect(Collectors.joining());
            String message = String.format("Found multiple service providers for %s%s", key.spi.getSimpleName(), names);
            throw new IllegalStateException(message);
        }

        //noinspection SequencedCollectionMethodCanBeUsed
        return providers.get(0);
    }

    public static <T> T get(Class<T> spi) {
        @SuppressWarnings("unchecked")
        ServiceLoader.Provider<T> provider =
                (ServiceLoader.Provider<T>) SERVICE_PROVIDERS.computeIfAbsent(
                        new CacheKey<>(spi),
                        SingleInstanceServiceLoader::compute
                );
        return provider.get();
    }

    private SingleInstanceServiceLoader() {}

    private record CacheKey<T>(Class<T> spi, ClassLoader classLoader) {
        CacheKey(Class<T> spi) {
            this(spi, Thread.currentThread().getContextClassLoader());
        }
    }
}
