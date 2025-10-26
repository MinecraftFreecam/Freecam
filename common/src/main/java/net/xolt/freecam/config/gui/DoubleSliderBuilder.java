package net.xolt.freecam.config.gui;

import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class DoubleSliderBuilder extends AbstractFieldBuilder<Double, DoubleSliderEntry, DoubleSliderBuilder> {
    private int precision = 2;
    private double min = 0;
    private double max;
    private double value;

    DoubleSliderBuilder(Component fieldName, Component resetButtonKey) {
        super(resetButtonKey, fieldName);
    }

    public DoubleSliderBuilder setPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    public DoubleSliderBuilder setMin(double min) {
        this.min = min;
        return this;
    }

    public DoubleSliderBuilder setMax(double max) {
        this.max = max;
        return this;
    }

    public DoubleSliderBuilder setValue(double value) {
        this.value = value;
        return this;
    }

    public @NotNull DoubleSliderEntry build() {
        DoubleSliderEntry entry = new DoubleSliderEntry(this.getFieldNameKey(), this.precision, this.min, this.max, this.value, this.getResetButtonKey(), this.getDefaultValue(), this.getSaveConsumer());

        entry.setTooltipSupplier(() -> this.getTooltipSupplier().apply(entry.getValue()));
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }

        return this.finishBuilding(entry);
    }
}