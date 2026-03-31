package net.xolt.freecam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.currentThread;

public class MultiServiceLoader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiServiceLoader.class);
    private static final Map<Class<?>, MultiServiceLoader<?>> SERVICE_LOADERS = new ConcurrentHashMap<>();

    private final Class<T> serviceClass;
    private final Map<ClassLoader, Collection<Provider<T>>> providers = new ConcurrentHashMap<>();

    private MultiServiceLoader(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public static <T> Collection<Provider<T>> get(Class<T> serviceClass) {
        return getServiceLoader(serviceClass).getProviders();
    }

    @SuppressWarnings("unchecked")
    private static <T> MultiServiceLoader<T> getServiceLoader(Class<T> serviceClass) {
        return (MultiServiceLoader<T>) SERVICE_LOADERS.computeIfAbsent(serviceClass, MultiServiceLoader::new);
    }

    private Collection<Provider<T>> getProviders() {
        return providers.computeIfAbsent(currentThread().getContextClassLoader(), this::loadProviders);
    }

    private Collection<Provider<T>> loadProviders(ClassLoader classLoader) {
        final String name = serviceClass.getSimpleName();
        LOGGER.debug("Loading service providers for {}", name);
        return ServiceLoader.load(serviceClass, classLoader).stream().toList();
    }
}
