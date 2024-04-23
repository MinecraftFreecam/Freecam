package net.xolt.freecam.config.gui;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.xolt.freecam.Freecam.MC;

/**
 * {@link IntegerSliderEntry} ported from {@code int} to {@code double}.
 */
class DoubleSliderEntry extends TooltipListEntry<Double> {
    private final Slider sliderWidget;
    private final Button resetButton;
    private final AtomicDouble value;
    private final double original;
    private final int precision;
    private final double minimum;
    private final double maximum;
    private final Supplier<Double> defaultValue;
    private final List<AbstractWidget> widgets;

    DoubleSliderEntry(Component fieldName, int precision, double minimum, double maximum, double value, Component resetText, Supplier<Double> defaultValue, @Nullable Consumer<Double> save) {
        //noinspection deprecation,UnstableApiUsage
        super(fieldName, null);
        this.value = new AtomicDouble(value);
        this.original = value;
        this.defaultValue = defaultValue;
        this.maximum = maximum;
        this.minimum = minimum;
        this.precision = precision;
        this.saveCallback = save;
        this.sliderWidget = new Slider(0, 0, 152, 20, (this.value.get() - minimum) / (maximum - minimum));
        this.sliderWidget.updateMessage();
        this.resetButton = new Button(0, 0, MC.font.width(resetText) + 6, 20, resetText, widget -> this.setValue(this.defaultValue.get()));
        this.widgets = List.of(this.sliderWidget, this.resetButton);
    }

    @Override
    public Double getValue() {
        return value.get();
    }

    public void setValue(double value) {
        double clamped = Mth.clamp(value, minimum, maximum);
        this.value.set(clamped);
        sliderWidget.setValue((clamped - minimum) / (maximum - minimum));
        sliderWidget.updateMessage();
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || getValue() != original;
    }

    @Override
    public Optional<Double> getDefaultValue() {
        return Optional.ofNullable(defaultValue).map(Supplier::get);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }

    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MC.getWindow();
        resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != value.get();
        resetButton.y = y;
        sliderWidget.active = isEditable();
        sliderWidget.y = y;

        Component name = getDisplayedFieldName();
        if (MC.font.isBidirectional()) {
            drawString(matrices, MC.font, name.getVisualOrderText(), window.getGuiScaledWidth() - x - MC.font.width(name), y + 6, getPreferredTextColor());
            resetButton.x = x;
            sliderWidget.x = x + resetButton.getWidth() + 1;
        } else {
            drawString(matrices, MC.font, name.getVisualOrderText(), x, y + 6, getPreferredTextColor());
            resetButton.x = x + entryWidth - resetButton.getWidth();
            sliderWidget.x = x + entryWidth - 150;
        }

        sliderWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(matrices, mouseX, mouseY, delta);
        sliderWidget.render(matrices, mouseX, mouseY, delta);
    }

    private final class Slider extends AbstractSliderButton {
        private Slider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, TextComponent.EMPTY, value);
        }

        @Override
        public void updateMessage() {
            NumberFormat fmt = DecimalFormat.getInstance();
            fmt.setMinimumIntegerDigits(1);
            fmt.setMinimumFractionDigits(precision);
            fmt.setMaximumFractionDigits(precision);
            setMessage(new TextComponent("Value: " + fmt.format(DoubleSliderEntry.this.value.get())));
        }

        @Override
        protected void applyValue() {
            double rounded = BigDecimal.valueOf(DoubleSliderEntry.this.minimum + (DoubleSliderEntry.this.maximum - DoubleSliderEntry.this.minimum) * this.value)
                    .setScale(DoubleSliderEntry.this.precision, RoundingMode.HALF_UP)
                    .doubleValue();
            DoubleSliderEntry.this.value.set(rounded);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return DoubleSliderEntry.this.isEditable() && super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            return DoubleSliderEntry.this.isEditable() && super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
