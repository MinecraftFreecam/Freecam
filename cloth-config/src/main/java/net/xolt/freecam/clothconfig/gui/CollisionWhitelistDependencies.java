package net.xolt.freecam.clothconfig.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.xolt.freecam.clothconfig.AutoConfigModConfig;

import static net.xolt.freecam.clothconfig.gui.AutoConfigExtensions.isField;

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
                            .reduce((prev, next) -> { throw new IllegalStateException("Multiple BooleanListEntries added to %s.ignoreCustom".formatted(AutoConfigModConfig.CollisionConfig.class.getSimpleName())); })
                            .orElseThrow(() -> new IllegalStateException("No BooleanListEntries added to %s.ignoreCustom".formatted(AutoConfigModConfig.CollisionConfig.class.getSimpleName())));
                    return guis;
                },
                isField(AutoConfigModConfig.CollisionConfig.class, "ignoreCustom")
        );

        // Whitelist group dependency
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    // FIXME requirements not supported by cloth 5.3.63
                    //? cloth: >5.3.63
                    guis.forEach(gui -> gui.setDisplayRequirement(() -> ignoreCustom == null || ignoreCustom.getValue()));
                    return guis;
                },
                isField(AutoConfigModConfig.CollisionConfig.class, "whitelist")
        );
    }
}
