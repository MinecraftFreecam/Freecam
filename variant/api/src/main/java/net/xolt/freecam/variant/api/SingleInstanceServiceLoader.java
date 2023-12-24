package net.xolt.freecam.variant.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

class SingleInstanceServiceLoader {

    private static final Map<Class<?>, Object> SERVICE_PROVIDERS = new HashMap<>();

    static <T> T get(Class<T> type) {
        return type.cast(SERVICE_PROVIDERS.computeIfAbsent(type, key -> {
            List<ServiceLoader.Provider<T>> providers = ServiceLoader.load(type).stream().toList();

            if (providers.isEmpty()) {
                String message = "Could not find any service providers for %s".formatted(type.getSimpleName());
                System.out.println(message);
                throw new IllegalStateException(message);
            }

            if (providers.size() > 1) {
                String message = "Found multiple service providers for %s%n%s".formatted(type.getSimpleName(),
                        providers.stream()
                                .map(provider -> provider.type().getSimpleName())
                                .map(s -> " - " + s)
                                .toList()
                                .toString());
                System.out.println(message);
                throw new IllegalStateException(message);
            }

            return providers.get(0).get();
        }));
    }

    private SingleInstanceServiceLoader() {}
}
