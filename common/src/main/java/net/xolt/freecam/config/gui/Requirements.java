package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.ValueHolder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.xolt.freecam.variant.api.BuildVariant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static java.lang.Boolean.FALSE;

@SuppressWarnings("UnstableApiUsage")
class Requirements {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ValueHolder<Boolean> ignoreAllWidget;

    private Requirements() {}

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
            if (BuildVariant.getInstance().name().equals("modrinth")) {
                // Disabling doesn't make sense on the modrinth build
                return guis;
            }
            guis.stream()
                    .filter(BooleanListEntry.class::isInstance)
                    .map(BooleanListEntry.class::cast)
                    .forEach(gui -> gui.setRequirement(Requirements::notIgnoreAll));
            return guis;
        }, field -> List.of("ignoreTransparent", "ignoreOpenable").contains(field.getName()));
    }

    // Requirement handler: require ignoreAll is set to "No"
    private static boolean notIgnoreAll() {
        return ignoreAllWidget == null || FALSE.equals(ignoreAllWidget.getValue());
    }
}
