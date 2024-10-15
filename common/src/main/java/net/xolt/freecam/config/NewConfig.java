package net.xolt.freecam.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.impl.builders.KeyCodeBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.config.gui.DoubleSliderEntry;
import net.xolt.freecam.variant.api.BuildVariant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class NewConfig {
    /**
     * Build a {@link ConfigBuilder} with our custom config + GUI screen
     *
     * @return the builder
     */
    public static ConfigBuilder builder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.translatable("text.autoconfig.freecam.title"))
                // .setGlobalized(true) // Adds a sidebar menu
                .transparentBackground()
                .setSavingRunnable(NewConfig::save);

        // Shared entry builder instance
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Create & populate the "default" top-level category
        // This mutates the builder by adding a new "category" to it
        defaultCategory(builder, entryBuilder);

        return builder;
    }

    private static void save() {
        // TODO
        // Serialise the config into the config file.
        // This will be called last after all variables are updated.
    }

    // TODO: do we need separate `builder` and `getConfigScreen` methods,
    //  or can we just have one single `buildConfigScreen` method?
    public static Screen getConfigScreen(Screen parent) {
        return builder().setParentScreen(parent).build();
    }

    /** Creates the top-level category.
     * Note: category tabs are only shown if multible cateories are registered. Maybe we should consider having category tabs instead of collapsible sub-categories?
     */
    private static ConfigCategory defaultCategory(ConfigBuilder configBuilder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory category = configBuilder.getOrCreateCategory(Component.empty());

        // Add entries to the category
        // Currently this will be sub-categories
        Stream.of(
                controlsCategory(entryBuilder),
                movementCategory(entryBuilder),
                collisionCategory(entryBuilder),
                visualCategory(entryBuilder),
                utilityCategory(entryBuilder),
                serversCategory(entryBuilder),
                notificationCategory(entryBuilder)
        ).forEach(category::addEntry);

        return category;
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the controls sub-category
     */
    private static SubCategoryListEntry controlsCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.controls"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.controls.@Tooltip"));

        // Add a KeyCodeEntry for each binding in ModBindings
        ModBindings.stream()
                .map(bind -> entryBuilder.fillKeybindingField(Component.translatable(bind.getName()), bind))
                .map(KeyCodeBuilder::build)
                .forEach(builder::add);

        return builder.build();
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the movement sub-category
     */
    private static SubCategoryListEntry movementCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.movement"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.@Tooltip"));

        EnumListEntry<ModConfig.FlightMode> flightMode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.movement.flightMode"),
                        ModConfig.FlightMode.class,
                        ModConfig.FlightMode.DEFAULT) // FIXME load from a real config
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.flightMode.@Tooltip"))
                .setDefaultValue(ModConfig.FlightMode.DEFAULT) // FIXME load from a config API
                // .setSaveConsumer() // TODO
                .build();

        DoubleSliderEntry horizontalSpeed = new DoubleSliderEntry(
                Component.translatable("text.autoconfig.freecam.option.movement.horizontalSpeed"),
                2,
                0,
                10,
                1, // FIXME load from config
                Component.translatable("text.cloth-config.reset_value"),
                () -> 1.0, // TODO get from config system
                val -> { } // TODO save to config

        );
        horizontalSpeed.setTooltipSupplier(() -> Optional.of(new Component[]{
                Component.translatable("text.autoconfig.freecam.option.movement.horizontalSpeed.@Tooltip")
        }));

        DoubleSliderEntry verticalSpeed = new DoubleSliderEntry(
                Component.translatable("text.autoconfig.freecam.option.movement.verticalSpeed"),
                2,
                0,
                10,
                1, // FIXME load from config
                Component.translatable("text.cloth-config.reset_value"),
                () -> 1.0, // TODO get from config system
                val -> { } // TODO save to config

        );
        verticalSpeed.setTooltipSupplier(() -> Optional.of(new Component[]{
                Component.translatable("text.autoconfig.freecam.option.movement.verticalSpeed.@Tooltip")
        }));

        // Add entries to the sub-category
        Stream.of(
                flightMode,
                horizontalSpeed,
                verticalSpeed
        ).forEach(builder::add);

        return builder.build();
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the collision sub-category
     */
    private static SubCategoryListEntry collisionCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.collision"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.@Tooltip"));

        BooleanListEntry ignoreTransparent = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.collision.ignoreTransparent"), false)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreTransparent.@Tooltip"))
                .setDefaultValue(false) // TODO get from config system
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry ignoreOpenable = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.collision.ignoreOpenable"), false)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreOpenable.@Tooltip"))
                .setDefaultValue(false) // TODO get from config
                // .setSaveConsumer() // TODO
                .build();

        List<Component> ignoreCustomTooltip = new ArrayList<>();
        ignoreCustomTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom.@Tooltip[0]"));
        if (BuildVariant.getInstance().name().equals("modrinth")) {
            ignoreCustomTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom.@ModrinthTooltip[1]"));
        }
        BooleanListEntry ignoreCustom = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom"), !BuildVariant.getInstance().name().equals("modrinth"))
                .setTooltip(ignoreCustomTooltip.toArray(Component[]::new))
                .setDefaultValue(!BuildVariant.getInstance().name().equals("modrinth")) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        StringListListEntry idWhitelist = entryBuilder.startStrList(Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids"), new ArrayList<>())
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids.@Tooltip[1]"))
                .setDefaultValue(Collections.emptyList())
                // .setSaveConsumer() // TODO
                .build();

        StringListListEntry patternWhitelist = entryBuilder.startStrList(Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns"), new ArrayList<>())
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns.@Tooltip[1]"))
                .setDefaultValue(Collections.emptyList())
                // .setSaveConsumer() // TODO
                .build();

        List<Component> ignoreAllTooltip = new ArrayList<>();
        ignoreAllTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@Tooltip[0]"));
        ignoreAllTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@Tooltip[1]"));
        if (BuildVariant.getInstance().name().equals("modrinth")) {
            ignoreAllTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@ModrinthTooltip[2]"));
        }
        BooleanListEntry ignoreAll = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll"), !BuildVariant.getInstance().name().equals("modrinth"))
                .setTooltip(ignoreAllTooltip.toArray(Component[]::new))
                .setDefaultValue(!BuildVariant.getInstance().name().equals("modrinth")) // TODO get from config system
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry alwaysCheck = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck"), false)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck.@Tooltip[1]"))
                .setDefaultValue(false) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        // Add entries to the sub-category
        Stream.of(
                ignoreTransparent,
                ignoreOpenable,
                ignoreCustom,
                idWhitelist,
                patternWhitelist,
                ignoreAll,
                alwaysCheck
        ).forEach(builder::add);

        return builder.build();
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the visual sub-category
     */
    private static SubCategoryListEntry visualCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.visual"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.@Tooltip"));

        EnumListEntry<ModConfig.Perspective> perspective = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.visual.perspective"),
                        ModConfig.Perspective.class,
                        ModConfig.Perspective.INSIDE) // FIXME load from a real config
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.perspective.@Tooltip"))
                .setDefaultValue(ModConfig.Perspective.INSIDE) // TODO get from config system
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry showPlayer = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.visual.showPlayer"), true)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showPlayer.@Tooltip"))
                .setDefaultValue(true) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry showHand = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.visual.showHand"), false)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showHand.@Tooltip"))
                .setDefaultValue(false) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry fullBright = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.visual.fullBright"), false)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.fullBright.@Tooltip"))
                .setDefaultValue(false) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry showSubmersion = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.visual.showSubmersion"), false)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showSubmersion.@Tooltip"))
                .setDefaultValue(false) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        // Add entries to the sub-category
        Stream.of(
                perspective,
                showPlayer,
                showHand,
                fullBright,
                showSubmersion
        ).forEach(builder::add);

        return builder.build();
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the utility sub-category
     */
    private static SubCategoryListEntry utilityCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.utility"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.@Tooltip"));

        BooleanListEntry disableOnDamage = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.utility.disableOnDamage"), true)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.disableOnDamage.@Tooltip"))
                .setDefaultValue(true) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry freezePlayer = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer"), false)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer.@Tooltip[0]"),
                        Component.translatable(
                                BuildVariant.getInstance().name().equals("modrinth")
                                ? "text.autoconfig.freecam.option.utility.freezePlayer.@ModrinthTooltip[1]"
                                : "text.autoconfig.freecam.option.utility.freezePlayer.@Tooltip[1]"
                        )
                )
                .setDefaultValue(false) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        List<Component> allowInteractTooltip = new ArrayList<>();
        allowInteractTooltip.add(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@Tooltip[0]"));
        allowInteractTooltip.add(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@Tooltip[1]"));
        if (BuildVariant.getInstance().name().equals("modrinth")) {
            allowInteractTooltip.add(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@ModrinthTooltip[2]"));
        }
        BooleanListEntry allowInteract = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract"), false)
                .setTooltip(allowInteractTooltip.toArray(Component[]::new))
                .setDefaultValue(false) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        EnumListEntry<ModConfig.InteractionMode> interactionMode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.utility.interactionMode"),
                        ModConfig.InteractionMode.class,
                        ModConfig.InteractionMode.CAMERA) // FIXME load from a real config
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.interactionMode.@Tooltip"))
                .setDefaultValue(ModConfig.InteractionMode.CAMERA) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        // Add entries to the sub-category
        Stream.of(
                disableOnDamage,
                freezePlayer,
                allowInteract,
                interactionMode
        ).forEach(builder::add);

        return builder.build();
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the servers sub-category
     */
    private static SubCategoryListEntry serversCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.servers"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.servers.@Tooltip"));

        EnumListEntry<ModConfig.ServerRestriction> mode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.servers.mode"),
                        ModConfig.ServerRestriction.class,
                        ModConfig.ServerRestriction.NONE) // FIXME load from a real config
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.servers.mode.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.servers.mode.@Tooltip[1]"))
                .setDefaultValue(ModConfig.ServerRestriction.NONE) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        StringListListEntry whitelist = entryBuilder.startStrList(Component.translatable("text.autoconfig.freecam.option.servers.whitelist"), new ArrayList<>())
                // .setTooltip()
                .setDefaultValue(Collections.emptyList()) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        StringListListEntry blacklist = entryBuilder.startStrList(Component.translatable("text.autoconfig.freecam.option.servers.blacklist"), new ArrayList<>())
                // .setTooltip()
                .setDefaultValue(Collections.emptyList()) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        // Add entries to the sub-category
        Stream.of(
                mode,
                whitelist,
                blacklist
        ).forEach(builder::add);

        return builder.build();
    }

    /**
     * @param entryBuilder {@link ConfigEntryBuilder cloth-config entry builder}
     * @return the notification sub-category
     */
    private static SubCategoryListEntry notificationCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.notification"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.@Tooltip"));

        BooleanListEntry notifyFreecam = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.notification.notifyFreecam"), true)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.notifyFreecam.@Tooltip"))
                .setDefaultValue(true) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        BooleanListEntry notifyTripod = entryBuilder.startBooleanToggle(Component.translatable("text.autoconfig.freecam.option.notification.notifyTripod"), true)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.notifyTripod.@Tooltip"))
                .setDefaultValue(true) // TODO get from config api
                // .setSaveConsumer() // TODO
                .build();

        // Add entries to the sub-category
        Stream.of(
                notifyFreecam,
                notifyTripod
        ).forEach(builder::add);

        return builder.build();
    }
}
