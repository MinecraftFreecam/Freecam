package net.xolt.freecam.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.DefaultGuiProviders;
import me.shedaniel.autoconfig.gui.DefaultGuiTransformers;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.variant.api.BuildVariant;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Extensions and modifications to AutoConfig.
 *
 * @see DefaultGuiProviders
 * @see DefaultGuiTransformers
 */
public class ConfigExtensions {

    private ConfigExtensions() {}

    public static void init(GuiRegistry registry) {

        registry.registerAnnotationTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    VariantTooltip tooltip = field.getAnnotation(VariantTooltip.class);
                    applyVariantTooltip(guis, Collections.singletonList(tooltip), i18n);
                    return guis;
                },
                VariantTooltip.class
        );

        registry.registerAnnotationTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    VariantTooltip.List tooltips = field.getAnnotation(VariantTooltip.List.class);
                    applyVariantTooltip(guis, Arrays.asList(tooltips.value()), i18n);
                    return guis;
                },
                VariantTooltip.List.class
        );
    }

    /**
     * Adds the tooltip to the specified GUIs that {@link TooltipListEntry support tooltips}.
     * <p>
     * Note: as-per {@link ConfigEntry.Gui.Tooltip}, tooltips will not be added to {@link TextListEntry}s.
     *
     * @param guis            the GUIs to which the tooltip should be added.
     * @param tooltipVariants the {@link VariantTooltip} annotations defining the tooltip.
     * @param i18n            the translation key for the config entries.
     * @see ConfigEntry.Gui.Tooltip
     */
    @Contract(mutates = "param1")
    private static void applyVariantTooltip(List<AbstractConfigListEntry> guis, List<VariantTooltip> tooltipVariants, String i18n) {
        String variant = BuildVariant.getInstance().name();

        // Number of tooltip lines defined for the current build variant (or "all")
        // (throw if there isn't exactly one matching definition)
        int count = tooltipVariants.stream()
                .filter(entry -> Objects.equals(variant, entry.variant()))
                .mapToInt(VariantTooltip::count)
                .reduce((prev, next) -> {
                    throw new IllegalArgumentException("%s: Multiple variants matching \"%s\" declared on \"%s\".".formatted(VariantTooltip.class.getSimpleName(), variant, i18n));
                })
                .orElseThrow(() -> new IllegalStateException("%s: No variants matching \"%s\" declared on \"%s\".".formatted(VariantTooltip.class.getSimpleName(), variant, i18n)));

        // Add tooltip to each of the fields guis that support tooltips
        // (except text entries)
        guis.stream()
                .filter(gui -> !(gui instanceof TextListEntry))
                .filter(TooltipListEntry.class::isInstance)
                .map(gui -> (TooltipListEntry<?>) gui)
                .forEach(gui -> gui.setTooltipSupplier(getTooltip(i18n, count)));
    }

    /**
     * Generates a tooltip supplier for the given base i18n key.
     *
     * @param i18n    the config entry's translation key.
     * @param count   the number of lines in the tooltip.
     * @return A tooltip supplier accepted by {@link TooltipListEntry#setTooltipSupplier(Supplier)}.
     * @see TooltipListEntry
     */
    private static Supplier<Optional<Component[]>> getTooltip(String i18n, int count) {
        if (count == 0) {
            return Optional::empty;
        }

        // We can cache the tooltip since language can't change while config GUI is open.
        Optional<Component[]> tooltip;
        if (count == 1) {
            tooltip = Optional.of(new Component[] { getTooltipLine(i18n, -1) });
        } else {
            tooltip = Optional.of(IntStream.range(0, count)
                    .mapToObj(i -> getTooltipLine(i18n, i))
                    .toArray(Component[]::new));
        }
        return () -> tooltip;
    }

    /**
     * Generates a tooltip line for the given base i18n key & line index combination.
     *
     * @param i18n    the config entry's translation key.
     * @param index   the line's index (or {@code -1}).
     * @return A line of {@link Component text} to be included in a wider tooltip.
     * @see #getTooltip(String, int)
     */
    private static Component getTooltipLine(String i18n, int index) {
        String key = "%s.@Tooltip".formatted(i18n);
        if (index > -1) {
            key += "[%d]".formatted(index);
        }
        return Component.translatable(key);
    }
}
