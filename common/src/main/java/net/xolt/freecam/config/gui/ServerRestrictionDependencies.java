package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.xolt.freecam.config.ModConfig;

import static net.xolt.freecam.config.ModConfig.ServerRestriction.BLACKLIST;
import static net.xolt.freecam.config.ModConfig.ServerRestriction.WHITELIST;
import static net.xolt.freecam.config.gui.AutoConfigExtensions.isField;

class ServerRestrictionDependencies {

    private static SelectionListEntry<ModConfig.ServerRestriction> mode;

    @SuppressWarnings("UnstableApiUsage")
    static void apply(GuiRegistry registry) {
        // Capture a reference to the mode entry
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    //noinspection unchecked
                    mode = guis.stream()
                            .filter(SelectionListEntry.class::isInstance)
                            .map(SelectionListEntry.class::cast)
                            .filter(entry -> entry.getValue() instanceof ModConfig.ServerRestriction)
                            .reduce((prev, next) -> { throw new IllegalStateException("Multiple SelectionListEntries added to %s.mode".formatted(ModConfig.ServerConfig.class.getSimpleName())); })
                            .orElseThrow(() -> new IllegalStateException("No SelectionListEntries added to %s.mode".formatted(ModConfig.ServerConfig.class.getSimpleName())));
                    return guis;
                },
                isField(ModConfig.ServerConfig.class, "mode")
        );

        // Whitelist dependency
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    // FIXME requirements not supported by cloth 5.3.63
                    //guis.forEach(gui -> gui.setDisplayRequirement(() -> mode == null || mode.getValue() == WHITELIST));
                    return guis;
                },
                isField(ModConfig.ServerConfig.class, "whitelist")
        );

        // Blacklist dependency
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    // FIXME requirements not supported by cloth 5.3.63
                    //guis.forEach(gui -> gui.setDisplayRequirement(() -> mode == null || mode.getValue() == BLACKLIST));
                    return guis;
                },
                isField(ModConfig.ServerConfig.class, "blacklist")
        );
    }
}
