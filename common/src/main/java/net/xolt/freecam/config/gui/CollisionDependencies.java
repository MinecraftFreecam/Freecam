package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//? if cloth: > 5.3.63 {
import me.shedaniel.clothconfig2.api.ValueHolder;
//? } else {
/*import me.shedaniel.clothconfig2.api.ReferenceProvider;
*///? }

import java.util.List;

import static java.lang.Boolean.FALSE;

@SuppressWarnings("UnstableApiUsage")
class CollisionDependencies {

    private static final Logger LOGGER = LogManager.getLogger();
    //? if cloth: > 5.3.63 {
    private static ValueHolder<Boolean> ignoreAllWidget;
    //? } else
    //private static ReferenceProvider<Boolean> ignoreAllWidget;

    private CollisionDependencies() {}

    static void apply(GuiRegistry guiRegistry) {
        // FIXME These transformers assume that no subsequent GUI transformers will replace
        //       the widgets. That's fine, so long as nothing changes, however a dedicated
        //       AutoConfig requirements API would be better.
        //
        // NOTE The Cloth Config Requirements API is currently marked "unstable", although
        //      significant changes seem unlikely.

        // Register a transformer to capture the ignoreAll GUI
        guiRegistry.registerPredicateTransformer((guis, i18n, field, config, defaults, registry) -> {
            // Filter out unrelated widgets, such as PrefixText.
            // Also allows us to safely cast.
            List<BooleanListEntry> widgets = guis.stream()
                    .filter(BooleanListEntry.class::isInstance)
                    .map(BooleanListEntry.class::cast)
                    .toList();
            if (widgets.isEmpty()) {
                LOGGER.error("Unable to find ignoreAll widget.");
                return guis;
            }
            if (widgets.size() > 1) {
                LOGGER.warn("Multiple ignoreAll widgets, choosing first.");
            }
            ignoreAllWidget = widgets.get(0);
            return guis;
        }, field -> field.getName().equals("ignoreAll"));

        // Register a transformer to set requirements for ignoreTransparent & ignoreOpenable
        guiRegistry.registerPredicateTransformer((guis, i18n, field, config, defaults, registry) -> {
            // FIXME requirements not supported by cloth 5.3.63
            //? cloth: > 5.3.63 {
            guis.stream()
                    .filter(BooleanListEntry.class::isInstance)
                    .map(BooleanListEntry.class::cast)
                    .forEach(gui -> gui.setRequirement(CollisionDependencies::notIgnoreAll));
            //? }
            return guis;
        }, field -> List.of("ignoreTransparent", "ignoreOpenable", "ignoreCustom").contains(field.getName()));
    }

    // Requirement handler: require ignoreAll is set to "No"
    private static boolean notIgnoreAll() {
        return ignoreAllWidget == null || FALSE.equals(ignoreAllWidget
                //? cloth: <= 5.3.63
                //.provideReferenceEntry()
                .getValue());
    }
}
