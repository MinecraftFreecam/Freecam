package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.xolt.freecam.config.ModConfig;

import static net.xolt.freecam.config.gui.AutoConfigExtensions.isField;

class CollisionWhitelistDependencies {

    private static BooleanListEntry ignoreCustom;

    @SuppressWarnings("UnstableApiUsage")
    static void apply(GuiRegistry registry) {
        // Capture a reference to the ignoreCustom entry
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    ignoreCustom = guis.stream()
                            .filter(BooleanListEntry.class::isInstance)
                            .map(BooleanListEntry.class::cast)
                            .reduce((prev, next) -> { throw new IllegalStateException("Multiple BooleanListEntries added to %s.ignoreCustom".formatted(ModConfig.CollisionConfig.class.getSimpleName())); })
                            .orElseThrow(() -> new IllegalStateException("No BooleanListEntries added to %s.ignoreCustom".formatted(ModConfig.CollisionConfig.class.getSimpleName())));
                    return guis;
                },
                isField(ModConfig.CollisionConfig.class, "ignoreCustom")
        );

        // Whitelist group dependency
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    // FIXME requirements not supported by cloth 5.3.63
                    //guis.forEach(gui -> gui.setDisplayRequirement(() -> ignoreCustom == null || ignoreCustom.getValue()));
                    return guis;
                },
                isField(ModConfig.CollisionConfig.class, "whitelist")
        );
    }
}
