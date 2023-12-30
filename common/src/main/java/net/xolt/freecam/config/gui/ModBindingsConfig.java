package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.xolt.freecam.config.ModBindings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds an {@link KeyCodeEntry entry} for each {@link ModBindings} to the annotated category.
 * <p>
 * This replaces whatever config entry GUIs would otherwise have been provided for the annotated field.
 * <p>
 * Can also be used together with {@link ConfigEntry.Gui.CollapsibleObject}, which places the entries in a collapsible
 * sub-category. When used without {@link ConfigEntry.Gui.CollapsibleObject}, entries are added transitively at the same
 * level as the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModBindingsConfig {}
