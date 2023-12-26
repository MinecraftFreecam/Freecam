package net.xolt.freecam.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.lang.annotation.*;

/**
 * Applies a tooltip to list entries that support it, defined in your lang file.
 * <p>
 * Will try to use translations defined for the current build variant (e.g. {@code @ModrinthTooltip}), but will
 * fall back to using the default {@code @Tooltip} translations if variant-specific ones are not defined.
 * <p>
 * Can be declared multiple times on the same field.
 *
 * @see ConfigEntry.Gui.Tooltip
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(VariantTooltip.List.class)
public @interface VariantTooltip {

    String variant() default "all";

    int count() default 1;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        VariantTooltip[] value();
    }
}
