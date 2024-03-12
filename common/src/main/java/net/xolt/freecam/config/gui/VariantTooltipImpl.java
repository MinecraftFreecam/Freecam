package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.variant.api.BuildVariant;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

class VariantTooltipImpl {

    private VariantTooltipImpl() {}

    static void apply(GuiRegistry registry) {

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
     * Adds the correct tooltip for the current build environment to the specified GUIs that support tooltips.
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
        List<String> variants = List.of(variant, "all");

        // Number of tooltip lines defined for the current build variant (or "all")
        // (throw if there isn't exactly one matching definition)
        int count = tooltipVariants.stream()
                .filter(entry -> variants.contains(entry.variant()))
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
                .forEach(gui -> gui.setTooltipSupplier(getVariantTooltip(variant, i18n, count)));
    }

    /**
     * Generates a tooltip supplier for the given variant & base i18n key combination.
     *
     * @param variant the current build variant.
     * @param i18n    the config entry's translation key.
     * @param count   the number of lines in the tooltip.
     * @return A tooltip supplier accepted by {@link TooltipListEntry#setTooltipSupplier(Supplier)}.
     * @see TooltipListEntry
     */
    private static Supplier<Optional<Component[]>> getVariantTooltip(String variant, String i18n, int count) {
        if (count == 0) {
            return Optional::empty;
        }

        // We can cache the tooltip since language can't change while config GUI is open.
        Optional<Component[]> tooltip;
        if (count == 1) {
            tooltip = Optional.of(new Component[] { getVariantTooltipLine(variant, i18n, -1) });
        } else {
            tooltip = Optional.of(IntStream.range(0, count)
                    .mapToObj(i -> getVariantTooltipLine(variant, i18n, i))
                    .toArray(Component[]::new));
        }
        return () -> tooltip;
    }

    /**
     * Generates a tooltip line for the given variant, base i18n key & line index combination.
     * <p>
     * Falls back to the default {@code @Tooltip} line if no key exists for the specified variant.
     *
     * @param variant the current build variant.
     * @param i18n    the config entry's translation key.
     * @param index   the line's index (or {@code -1}).
     * @return A line of {@link Component text} to be included in a wider tooltip.
     * @see #getVariantTooltip(String, String, int)
     */
    private static Component getVariantTooltipLine(String variant, String i18n, int index) {
        String key = "%s.@%sTooltip".formatted(i18n, StringUtils.capitalize(variant));
        if (index > -1) {
            key += "[%d]".formatted(index);
        }
        // FIXME how will this behave for untranslated languages?
        if (Language.getInstance().has(key)) {
            return Component.translatable(key);
        }
        if (variant.isEmpty()) {
            return Component.empty();
        }
        // Fallback to default "@Tooltip" translation
        return getVariantTooltipLine("", i18n, index);
    }
}
