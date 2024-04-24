package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.gui.DefaultGuiProviders;
import me.shedaniel.autoconfig.gui.DefaultGuiTransformers;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Extensions and modifications to AutoConfig.
 *
 * @see DefaultGuiProviders
 * @see DefaultGuiTransformers
 */
@SuppressWarnings("JavadocReference")
public class AutoConfigExtensions {
    static final Component RESET_TEXT = new TranslatableComponent("text.cloth-config.reset_value");
    static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

    private AutoConfigExtensions() {}

    public static void apply(Class<? extends ConfigData> configClass) {
        GuiRegistry registry = AutoConfig.getGuiRegistry(configClass);

        ModBindingsConfigImpl.apply(registry);
        VariantTooltipImpl.apply(registry);
        ValidateRegexImpl.apply(registry);
        BoundedContinuousImpl.apply(registry);

        CollisionDependencies.apply(registry);
        CollisionWhitelistDependencies.apply(registry);
        ServerRestrictionDependencies.apply(registry);
    }

    static Predicate<Field> isField(Class<?> declaringClass, String... fieldNames) {
        return field -> field.getDeclaringClass().equals(declaringClass) && Arrays.asList(fieldNames).contains(field.getName());
    }

    static Predicate<Field> isArrayOrListOfType(Type... types) {
        return field -> {
            if (field.getType().isArray()) {
                Class<?> component = field.getType().getComponentType();
                return Arrays.asList(types).contains(component);
            }
            return isListOfType(types).test(field);
        };
    }

    static Predicate<Field> isNotArrayOrListOfType(Type... types) {
        return field -> {
            if (field.getType().isArray()) {
                Class<?> component = field.getType().getComponentType();
                return Arrays.stream(types).noneMatch(component::equals);
            }
            return isNotListOfType(types).test(field);
        };
    }

    /**
     * Returns a predicate that tests if the field is a {@link List} of a particular {@link Type}, e.g. a {@code List<Integer>}.
     * <p>
     * Based on a {@link DefaultGuiProviders#isListOfType(Type...) helper method} in cloth-config.
     *
     * @param types the types to check for in the list's parameter
     * @return {@code true} if the field is a list containing the provided type, {@code false} otherwise
     */
    static Predicate<Field> isListOfType(Type... types) {
        return field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType generic = (ParameterizedType)field.getGenericType();
                Type[] args = generic.getActualTypeArguments();
                return args.length == 1 && Arrays.asList(types).contains(args[0]);
            } else {
                return false;
            }
        };
    }

    /**
     * Returns a predicate that tests if the field is a {@link List} <strong>not</strong> of a particular {@link Type},
     * e.g. any {@code List} that isn't a {@code List<Integer>}.
     * <p>
     * Based on a {@link DefaultGuiProviders#isNotListOfType(Type...) helper method} in cloth-config.
     *
     * @param types the types to check for in the list's parameter
     * @return {@code true} if the field is a list not containing the provided type, {@code false} otherwise
     */
    static Predicate<Field> isNotListOfType(Type... types) {
        return field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType generic = (ParameterizedType)field.getGenericType();
                Type[] args = generic.getActualTypeArguments();
                return args.length == 1 && Arrays.stream(types).noneMatch(args[0]::equals);
            } else {
                return false;
            }
        };
    }
}
