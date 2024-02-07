package net.xolt.freecam.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.lang.annotation.*;

/**
 * Applies a tooltip to list entries that support it, defined in your lang file.
 * <p>
 * Should be used over {@link ConfigEntry.Gui.Tooltip} when {@link #count()} varies between variants.
 * <p>
 * Can be declared multiple times on the same field.
 *
 * @see ConfigEntry.Gui.Tooltip
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(VariantTooltip.List.class)
public @interface VariantTooltip {

    String variant();

    int count() default 1;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        VariantTooltip[] value();
    }
}
