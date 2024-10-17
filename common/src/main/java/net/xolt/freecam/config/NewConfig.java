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

    // Serialise the config into the config file.
    // This will be called last after all variables are updated.
    private static void save() {
        try {
            ModConfig.NEW_SERIALIZER.serialize(ModConfig.INSTANCE);
        } catch (Exception e) {
            // TODO: do we need to handle this?
            throw new RuntimeException(e);
        }

        // Invoke "on save" listeners
        // TODO: consider having a Collection<Runnable> to store handlers
        CollisionBehavior.onConfigChange(ModConfig.INSTANCE);
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
                        ModConfig.INSTANCE.movement.flightMode)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.flightMode.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.movement.flightMode)
                .setSaveConsumer(value -> ModConfig.INSTANCE.movement.flightMode = value)
                .build();

        DoubleSliderEntry horizontalSpeed = new DoubleSliderEntry(
                Component.translatable("text.autoconfig.freecam.option.movement.horizontalSpeed"),
                2,
                0,
                10,
                ModConfig.INSTANCE.movement.horizontalSpeed,
                Component.translatable("text.cloth-config.reset_value"),
                () -> ModConfig.DEFAULTS.movement.horizontalSpeed,
                value -> ModConfig.INSTANCE.movement.horizontalSpeed = value
        );
        horizontalSpeed.setTooltipSupplier(() -> Optional.of(new Component[]{
                Component.translatable("text.autoconfig.freecam.option.movement.horizontalSpeed.@Tooltip")
        }));

        DoubleSliderEntry verticalSpeed = new DoubleSliderEntry(
                Component.translatable("text.autoconfig.freecam.option.movement.verticalSpeed"),
                2,
                0,
                10,
                ModConfig.INSTANCE.movement.verticalSpeed,
                Component.translatable("text.cloth-config.reset_value"),
                () -> ModConfig.DEFAULTS.movement.verticalSpeed,
                value -> ModConfig.INSTANCE.movement.verticalSpeed = value

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

        BooleanListEntry ignoreTransparent = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreTransparent"),
                        ModConfig.INSTANCE.collision.ignoreTransparent)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreTransparent.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.collision.ignoreTransparent)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.ignoreTransparent = value)
                .build();

        BooleanListEntry ignoreOpenable = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreOpenable"),
                        ModConfig.INSTANCE.collision.ignoreOpenable)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreOpenable.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.collision.ignoreOpenable)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.ignoreOpenable = value)
                .build();

        List<Component> ignoreCustomTooltip = new ArrayList<>();
        ignoreCustomTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom.@Tooltip[0]"));
        if (BuildVariant.getInstance().name().equals("modrinth")) {
            ignoreCustomTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom.@ModrinthTooltip[1]"));
        }
        BooleanListEntry ignoreCustom = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom"),
                        ModConfig.INSTANCE.collision.ignoreCustom)
                .setTooltip(ignoreCustomTooltip.toArray(Component[]::new))
                .setDefaultValue(ModConfig.DEFAULTS.collision.ignoreCustom)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.ignoreCustom = value)
                .build();

        StringListListEntry idWhitelist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids"),
                        ModConfig.INSTANCE.collision.whitelist.ids)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids.@Tooltip[1]"))
                .setDisplayRequirement(ignoreCustom::getValue)
                .setDefaultValue(ModConfig.DEFAULTS.collision.whitelist.ids)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.whitelist.ids = value)
                .build();

        StringListListEntry patternWhitelist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns"),
                        ModConfig.INSTANCE.collision.whitelist.patterns)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns.@Tooltip[1]"))
                .setDisplayRequirement(ignoreCustom::getValue)
                .setDefaultValue(ModConfig.DEFAULTS.collision.whitelist.patterns)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.whitelist.patterns = value)
                .build();

        List<Component> ignoreAllTooltip = new ArrayList<>();
        ignoreAllTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@Tooltip[0]"));
        ignoreAllTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@Tooltip[1]"));
        if (BuildVariant.getInstance().name().equals("modrinth")) {
            ignoreAllTooltip.add(Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@ModrinthTooltip[2]"));
        }
        BooleanListEntry ignoreAll = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll"),
                        ModConfig.INSTANCE.collision.ignoreAll)
                .setTooltip(ignoreAllTooltip.toArray(Component[]::new))
                .setDefaultValue(ModConfig.DEFAULTS.collision.ignoreAll)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.ignoreAll = value)
                .build();

        BooleanListEntry alwaysCheck = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck"),
                        ModConfig.INSTANCE.collision.alwaysCheck)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck.@Tooltip[1]"))
                .setDefaultValue(ModConfig.DEFAULTS.collision.alwaysCheck)
                .setSaveConsumer(value -> ModConfig.INSTANCE.collision.alwaysCheck = value)
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
                        ModConfig.INSTANCE.visual.perspective)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.perspective.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.visual.perspective)
                .setSaveConsumer(value -> ModConfig.INSTANCE.visual.perspective = value)
                .build();

        BooleanListEntry showPlayer = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.showPlayer"),
                        ModConfig.INSTANCE.visual.showPlayer)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showPlayer.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.visual.showPlayer)
                .setSaveConsumer(value -> ModConfig.INSTANCE.visual.showPlayer = value)
                .build();

        BooleanListEntry showHand = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.showHand"),
                        ModConfig.INSTANCE.visual.showHand)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showHand.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.visual.showHand)
                .setSaveConsumer(value -> ModConfig.INSTANCE.visual.showHand = value)
                .build();

        BooleanListEntry fullBright = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.fullBright"),
                        ModConfig.INSTANCE.visual.fullBright)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.fullBright.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.visual.fullBright)
                .setSaveConsumer(value -> ModConfig.INSTANCE.visual.fullBright = value)
                .build();

        BooleanListEntry showSubmersion = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.showSubmersion"),
                        ModConfig.INSTANCE.visual.showSubmersion)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showSubmersion.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.visual.showSubmersion)
                .setSaveConsumer(value -> ModConfig.INSTANCE.visual.showSubmersion = value)
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

        BooleanListEntry disableOnDamage = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.utility.disableOnDamage"),
                        ModConfig.INSTANCE.utility.disableOnDamage)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.disableOnDamage.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.utility.disableOnDamage)
                .setSaveConsumer(value -> ModConfig.INSTANCE.utility.disableOnDamage = value)
                .build();

        BooleanListEntry freezePlayer = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer"),
                        ModConfig.INSTANCE.utility.freezePlayer)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer.@Tooltip[0]"),
                        Component.translatable(
                                BuildVariant.getInstance().name().equals("modrinth")
                                ? "text.autoconfig.freecam.option.utility.freezePlayer.@ModrinthTooltip[1]"
                                : "text.autoconfig.freecam.option.utility.freezePlayer.@Tooltip[1]"
                        )
                )
                .setDefaultValue(ModConfig.DEFAULTS.utility.freezePlayer)
                .setSaveConsumer(value -> ModConfig.INSTANCE.utility.freezePlayer = value)
                .build();

        List<Component> allowInteractTooltip = new ArrayList<>();
        allowInteractTooltip.add(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@Tooltip[0]"));
        allowInteractTooltip.add(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@Tooltip[1]"));
        if (BuildVariant.getInstance().name().equals("modrinth")) {
            allowInteractTooltip.add(Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@ModrinthTooltip[2]"));
        }
        BooleanListEntry allowInteract = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.utility.allowInteract"),
                        ModConfig.INSTANCE.utility.allowInteract)
                .setTooltip(allowInteractTooltip.toArray(Component[]::new))
                .setDefaultValue(ModConfig.DEFAULTS.utility.allowInteract)
                .setSaveConsumer(value -> ModConfig.INSTANCE.utility.allowInteract = value)
                .build();

        EnumListEntry<ModConfig.InteractionMode> interactionMode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.utility.interactionMode"),
                        ModConfig.InteractionMode.class,
                        ModConfig.INSTANCE.utility.interactionMode)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.interactionMode.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.utility.interactionMode)
                .setSaveConsumer(value -> ModConfig.INSTANCE.utility.interactionMode = value)
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
                        ModConfig.INSTANCE.servers.mode)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.servers.mode.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.servers.mode.@Tooltip[1]"))
                .setDefaultValue(ModConfig.DEFAULTS.servers.mode)
                .setSaveConsumer(value -> ModConfig.INSTANCE.servers.mode = value)
                .build();

        StringListListEntry whitelist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.servers.whitelist"),
                        ModConfig.INSTANCE.servers.whitelist)
                // .setTooltip()
                .setDisplayRequirement(() -> mode.getValue() == ModConfig.ServerRestriction.WHITELIST)
                .setDefaultValue(ModConfig.DEFAULTS.servers.whitelist)
                .setSaveConsumer(value -> ModConfig.INSTANCE.servers.whitelist = value)
                .build();

        StringListListEntry blacklist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.servers.blacklist"),
                        ModConfig.INSTANCE.servers.blacklist)
                // .setTooltip()
                .setDisplayRequirement(() -> mode.getValue() == ModConfig.ServerRestriction.BLACKLIST)
                .setDefaultValue(ModConfig.DEFAULTS.servers.whitelist)
                .setSaveConsumer(value -> ModConfig.INSTANCE.servers.whitelist = value)
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

        BooleanListEntry notifyFreecam = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.notification.notifyFreecam"),
                        ModConfig.INSTANCE.notification.notifyFreecam)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.notifyFreecam.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.notification.notifyFreecam)
                .setSaveConsumer(value -> ModConfig.INSTANCE.notification.notifyFreecam = value)
                .build();

        BooleanListEntry notifyTripod = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.notification.notifyTripod"),
                        ModConfig.INSTANCE.notification.notifyTripod)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.notifyTripod.@Tooltip"))
                .setDefaultValue(ModConfig.DEFAULTS.notification.notifyTripod)
                .setSaveConsumer(value -> ModConfig.INSTANCE.notification.notifyTripod = value)
                .build();

        // Add entries to the sub-category
        Stream.of(
                notifyFreecam,
                notifyTripod
        ).forEach(builder::add);

        return builder.build();
    }
}
