package net.xolt.freecam.variant.api;

import java.util.*;

class SingleInstanceServiceLoader {

    private static final Map<Class<?>, Object> SERVICE_PROVIDERS = new HashMap<>();

    static <T> T get(Class<T> type) {
        return type.cast(SERVICE_PROVIDERS.computeIfAbsent(type, key -> {
            T value = null;
            List<String> names = new ArrayList<>();

            for (T service : ServiceLoader.load(type)) {
                value = service;
                names.add(service.getClass().getSimpleName());
            }

            if (value == null) {
                String message = String.format("Could not find any service providers for %s", type.getSimpleName());
                System.out.println(message);
                throw new IllegalStateException(message);
            }

            if (names.size() > 1) {
                String message = String.format("Found multiple service providers for %s%n%s", type.getSimpleName(), names);
                System.out.println(message);
                throw new IllegalStateException(message);
            }

            return value;
        }));
    }

    private SingleInstanceServiceLoader() {}
}
