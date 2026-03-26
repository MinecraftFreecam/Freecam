package net.xolt.freecam.clothconfig.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.xolt.freecam.clothconfig.AutoConfigModConfig;

import static net.xolt.freecam.clothconfig.AutoConfigModConfig.ServerRestriction.BLACKLIST;
import static net.xolt.freecam.clothconfig.AutoConfigModConfig.ServerRestriction.WHITELIST;
import static net.xolt.freecam.clothconfig.gui.AutoConfigExtensions.isField;

class ServerRestrictionDependencies {

    private static SelectionListEntry<AutoConfigModConfig.ServerRestriction> mode;

    @SuppressWarnings("UnstableApiUsage")
    static void apply(GuiRegistry registry) {
        // Capture a reference to the mode entry
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    //noinspection unchecked
                    mode = guis.stream()
                            .filter(SelectionListEntry.class::isInstance)
                            .map(SelectionListEntry.class::cast)
                            .filter(entry -> entry.getValue() instanceof AutoConfigModConfig.ServerRestriction)
                            .reduce((prev, next) -> { throw new IllegalStateException("Multiple SelectionListEntries added to %s.mode".formatted(AutoConfigModConfig.ServerConfig.class.getSimpleName())); })
                            .orElseThrow(() -> new IllegalStateException("No SelectionListEntries added to %s.mode".formatted(AutoConfigModConfig.ServerConfig.class.getSimpleName())));
                    return guis;
                },
                isField(AutoConfigModConfig.ServerConfig.class, "mode")
        );

        // Whitelist dependency
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    // FIXME requirements not supported by cloth 5.3.63
                    //? cloth: >5.3.63
                    guis.forEach(gui -> gui.setDisplayRequirement(() -> mode == null || mode.getValue() == WHITELIST));
                    return guis;
                },
                isField(AutoConfigModConfig.ServerConfig.class, "whitelist")
        );

        // Blacklist dependency
        registry.registerPredicateTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    // FIXME requirements not supported by cloth 5.3.63
                    //? cloth: >5.3.63
                    guis.forEach(gui -> gui.setDisplayRequirement(() -> mode == null || mode.getValue() == BLACKLIST));
                    return guis;
                },
                isField(AutoConfigModConfig.ServerConfig.class, "blacklist")
        );
    }
}
