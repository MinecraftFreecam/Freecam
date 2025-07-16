package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.util.Utils;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.function.Supplier;

import static net.xolt.freecam.config.gui.AutoConfigExtensions.RESET_TEXT;

class BoundedContinuousImpl {

    private BoundedContinuousImpl() {}

    static void apply(GuiRegistry registry) {

        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    BoundedContinuous bounds = field.getAnnotation(BoundedContinuous.class);
                    DoubleSliderEntry entry = DoubleSliderEntry.builder(Component.translatable(i18n), RESET_TEXT)
                            .setPrecision(bounds.precision())
                            .setMin(bounds.min())
                            .setMax(bounds.max())
                            .setValue(Utils.getUnsafely(field, config, 0))
                            .setDefaultValue(() -> Utils.getUnsafely(field, defaults))
                            .setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue))
                            .build();
                    return Collections.singletonList(entry);
                },
                field -> field.getType() == Double.TYPE || field.getType() == Double.class,
                BoundedContinuous.class
        );
    }
}
