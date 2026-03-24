package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.xolt.freecam.config.TranslationName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Like {@link EnumHandler} in {@link EnumHandler.EnumDisplayOption BUTTON} mode,
 * but automatically adds translation keys to enum values derived from the annotated field.
 * <p>
 * Enum constants can be annotated with {@link TranslationName}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TranslatableEnumButton { }
