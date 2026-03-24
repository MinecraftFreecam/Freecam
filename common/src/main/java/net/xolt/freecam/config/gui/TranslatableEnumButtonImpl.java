package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.util.Utils;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.config.TranslationName;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static net.xolt.freecam.config.gui.AutoConfigExtensions.ENTRY_BUILDER;

class TranslatableEnumButtonImpl {

    private static final Map<Enum<?>, String> ENUM_KEY_CACHE = new ConcurrentHashMap<>();

    private TranslatableEnumButtonImpl() {}

    static void apply(GuiRegistry registry) {
        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ENTRY_BUILDER.startSelector(
                                        Component.translatable(i18n),
                                        Arrays.stream(field.getType().getEnumConstants())
                                                .map(Enum.class::cast)
                                                .toArray(Enum[]::new),
                                        Utils.getUnsafely(field, config, Utils.getUnsafely(field, defaults))
                                )
                                .setNameProvider(getNameProvider(i18n))
                                .setDefaultValue(() -> Utils.getUnsafely(field, defaults))
                                .setSaveConsumer(value -> Utils.setUnsafely(field, config, value))
                                .build()
                ),
                field -> field.getType().isEnum(),
                TranslatableEnumButton.class
        );
    }

    private static Function<Enum, Component> getNameProvider(String i18n) {
        String prefix = i18n + ".";
        return value -> Component.translatable(prefix + getTranslationName(value));
    }

    private static String getTranslationName(Enum<?> value) {
        return ENUM_KEY_CACHE.computeIfAbsent(value, v -> {
            try {
                TranslationName annotation = v.getDeclaringClass()
                        .getField(v.name())
                        .getAnnotation(TranslationName.class);

                if (annotation != null) return annotation.value();
            } catch (NoSuchFieldException ignored) {}

            return v.name().toLowerCase();
        });
    }
}
