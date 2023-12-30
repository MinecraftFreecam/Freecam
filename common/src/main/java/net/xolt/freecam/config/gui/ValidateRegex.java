package net.xolt.freecam.config.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Show an error message when the config entry is not a valid {@link java.util.regex.Pattern regex}.
 * <p>
 * Can be applied to a {@link String} or {@link java.util.List List<String>}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateRegex {}
