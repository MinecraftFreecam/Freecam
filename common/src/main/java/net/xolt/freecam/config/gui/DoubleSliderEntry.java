package net.xolt.freecam.config.gui;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.platform.Window;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.11 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//? }
//? if >=1.20.6 {
import net.minecraft.client.gui.GuiGraphics;
//? } else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///? }

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
        int sliderWidth = MC.font.width(resetText) + 6;
        Button.OnPress sliderOnPress = widget -> this.setValue(this.defaultValue.get());
        this.resetButton =
                //? if > 1.18.2 {
                Button.builder(resetText, sliderOnPress)
                        .width(sliderWidth)
                        .build();
                //? } else {
                /*new Button(0, 0,
                        sliderWidth, 20,
                        resetText, sliderOnPress);
                *///? }
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
    public void render(
            //? if >=1.20.6 {
            GuiGraphics
            //? } else
            //PoseStack
                    graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MC.getWindow();
        resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != value.get();
        sliderWidget.active = isEditable();
        //? if > 1.18.2 {
        resetButton.setY(y);
        sliderWidget.setY(y);
        //? } else {
        /*resetButton.y = y;
        sliderWidget.y = y;
        *///? }

        Component name = getDisplayedFieldName();
        if (MC.font.isBidirectional()) {
            //? if >=1.20.6 {
            graphics.drawString(
            //? } else
            //drawString(graphics,
                    MC.font, name.getVisualOrderText(), window.getGuiScaledWidth() - x - MC.font.width(name), y + 6, getPreferredTextColor());
            int sliderX = x + resetButton.getWidth() + 1;
            //? if > 1.18.2 {
            resetButton.setX(x);
            sliderWidget.setX(sliderX);
            //? } else {
            /*resetButton.x = x;
            sliderWidget.x = sliderX;
            *///? }
        } else {
            //? if >=1.20.6 {
            graphics.drawString(
            //? } else
            //drawString(graphics,
                    MC.font, name.getVisualOrderText(), x, y + 6, getPreferredTextColor());
            int resetX = x + entryWidth - resetButton.getWidth();
            int sliderX = x + entryWidth - 150;
            //? if > 1.18.2 {
            resetButton.setX(resetX);
            sliderWidget.setX(sliderX);
            //? } else {
            /*resetButton.x = resetX;
            sliderWidget.x = sliderX;
            *///? }
        }

        sliderWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(graphics, mouseX, mouseY, delta);
        sliderWidget.render(graphics, mouseX, mouseY, delta);
    }

    private final class Slider extends AbstractSliderButton {
        private Slider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, Component.empty(), value);
        }

        @Override
        public void updateMessage() {
            NumberFormat fmt = DecimalFormat.getInstance();
            fmt.setMinimumIntegerDigits(1);
            fmt.setMinimumFractionDigits(precision);
            fmt.setMaximumFractionDigits(precision);
            setMessage(Component.literal("Value: " + fmt.format(DoubleSliderEntry.this.value.get())));
        }

        @Override
        protected void applyValue() {
            double rounded = BigDecimal.valueOf(DoubleSliderEntry.this.minimum + (DoubleSliderEntry.this.maximum - DoubleSliderEntry.this.minimum) * this.value)
                    .setScale(DoubleSliderEntry.this.precision, RoundingMode.HALF_UP)
                    .doubleValue();
            DoubleSliderEntry.this.value.set(rounded);
        }

        @Override
        public boolean keyPressed(
                //? if >=1.21.11 {
                KeyEvent event
                //? } else
                //int keyCode, int scanCode, int modifiers
        ) {
            return DoubleSliderEntry.this.isEditable() && super.keyPressed(
                    //? if >=1.21.11 {
                    event
                    //? } else
                    //keyCode, scanCode, modifiers
            );
        }

        @Override
        public boolean mouseDragged(
                //? if >=1.21.11 {
                MouseButtonEvent event, double double_3, double double_4
                //? } else
                //double mouseX, double mouseY, int button, double dragX, double dragY
        ) {
            return DoubleSliderEntry.this.isEditable() && super.mouseDragged(
                    //? if >=1.21.11 {
                    event, double_3, double_4
                    //? } else
                    //mouseX, mouseY, button, dragX, dragY
            );
        }

        //? if >=1.21.11
        @Override
        public void setValue(double value) {
            //? if >=1.21.11 {
            super.setValue(value);
            //? } else
            //this.value = value;
        }
    }
}
