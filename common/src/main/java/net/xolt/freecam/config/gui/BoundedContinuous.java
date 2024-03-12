package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A continuous version of {@link ConfigEntry.BoundedDiscrete}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundedContinuous {

        /**
         * The number of decimal places the slider will round to.
         */
        int precision() default 2;

        double min() default 0;
        double max();
}
