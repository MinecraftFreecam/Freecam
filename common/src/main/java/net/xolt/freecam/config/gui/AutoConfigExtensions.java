package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.gui.DefaultGuiProviders;
import me.shedaniel.autoconfig.gui.DefaultGuiTransformers;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Extensions and modifications to AutoConfig.
 *
 * @see DefaultGuiProviders
 * @see DefaultGuiTransformers
 */
public class AutoConfigExtensions {
    static final Component RESET_TEXT = Component.translatable("text.cloth-config.reset_value");
    static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

    private AutoConfigExtensions() {}

    public static void apply(Class<? extends ConfigData> configClass) {
        GuiRegistry registry = AutoConfig.getGuiRegistry(configClass);
        Requirements.apply(registry);
        ModBindingsConfigImpl.apply(registry);
        VariantTooltipImpl.apply(registry);
        BoundedContinuousImpl.apply(registry);
        ServerRestrictionDependencies.apply(registry);
    }

    static Predicate<Field> isField(Class<?> declaringClass, String... fieldNames) {
        return field -> field.getDeclaringClass().equals(declaringClass) && Arrays.asList(fieldNames).contains(field.getName());
    }
}
