package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.impl.builders.KeyCodeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.xolt.freecam.config.ModBindings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.xolt.freecam.config.gui.AutoConfigExtensions.ENTRY_BUILDER;

class ModBindingsConfigImpl {

    private ModBindingsConfigImpl() {}

    static void apply(GuiRegistry registry) {

        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> modBindingEntries(),
                field -> !field.isAnnotationPresent(ConfigEntry.Gui.CollapsibleObject.class),
                ModBindingsConfig.class
        );

        registry.registerAnnotationProvider(
                (i18n, field, config, defaults, guiProvider) -> {
                    ENTRY_BUILDER.startSubCategory(new TranslatableComponent(i18n), modBindingEntries())
                            .setExpanded(field.getDeclaredAnnotation(ConfigEntry.Gui.CollapsibleObject.class).startExpanded())
                            .build();
                    return new ArrayList<>(modBindingEntries());
                },
                field -> field.isAnnotationPresent(ConfigEntry.Gui.CollapsibleObject.class),
                ModBindingsConfig.class
        );
    }

    /**
     * @return a {@link KeyCodeEntry} for each binding in {@link ModBindings}.
     */
    private static List<AbstractConfigListEntry> modBindingEntries() {
        return ModBindings.stream()
                .map(bind -> ENTRY_BUILDER.fillKeybindingField(new TranslatableComponent(bind.getName()), bind))
                .map(KeyCodeBuilder::build)
                .map(AbstractConfigListEntry.class::cast)
                .collect(Collectors.toList());
    }
}
