package net.xolt.freecam.clothconfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.KeyCodeBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.config.model.ConfigController;
import net.xolt.freecam.config.model.FlightMode;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.clothconfig.model.ModConfigDTO;
import net.xolt.freecam.config.model.Perspective;

import java.util.stream.Stream;

//? if cloth_dependencies
@SuppressWarnings("UnstableApiUsage")
public class ModConfigScreenFactory {

    // ClothConfig doesn't have built-in double sliders, so scale up long sliders
    // UI displays 2dp, so scale by 100x
    private static final double SLIDER_SCALE = 100.0;
    private static final double MIN_SPEED = 0.0;
    private static final double MAX_SPEED = 10.0;

    private final ConfigController<ModConfigDTO> controller;

    public ModConfigScreenFactory(ConfigController<ModConfigDTO> controller) {
        this.controller = controller;
    }

    public Screen getConfigScreen(Screen parent) {
        return builder().setParentScreen(parent).build();
    }

    private ModConfigDTO config() {
        return controller.getConfig();
    }

    private ModConfigDTO defaults() {
        return controller.getDefaults();
    }

    /**
     * Construct a {@link ConfigBuilder} using our custom config & GUI screen.
     */
    private ConfigBuilder builder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.translatable("text.autoconfig.freecam.title"))
                // .setGlobalized(true) // Adds a sidebar menu
                .transparentBackground()
                .setSavingRunnable(controller::save);

        // Shared entry builder instance
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Create & populate the "default" top-level category
        // This mutates the builder by adding a new "category" to it
        ConfigCategory category = defaultCategory(builder, entryBuilder);

        return builder;
    }

    /**
     * Creates the default category.
     * <p>
     * Note: if multiple categories are registered, they'll show as tabs.
     * Currently we use sub-category list-entries instead, which show as collapsible menus.
     */
    private ConfigCategory defaultCategory(ConfigBuilder configBuilder, ConfigEntryBuilder entryBuilder) {
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
    private SubCategoryListEntry movementCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.movement"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.@Tooltip"));

        EnumListEntry<FlightMode> flightMode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.movement.flightMode"),
                        FlightMode.class,
                        config().movement.flightMode)
                .setEnumNameProvider(value -> Component.translatable("text.autoconfig.freecam.option.movement.flightMode." + value.name().toLowerCase()))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.flightMode.@Tooltip"))
                .setDefaultValue(defaults().movement.flightMode)
                .setSaveConsumer(value -> config().movement.flightMode = value)
                .build();

        LongSliderEntry horizontalSpeed = entryBuilder.startLongSlider(
                        Component.translatable("text.autoconfig.freecam.option.movement.horizontalSpeed"),
                        toSlider(config().movement.horizontalSpeed),
                        toSlider(MIN_SPEED), toSlider(MAX_SPEED))
                .setDefaultValue(() -> toSlider(defaults().movement.horizontalSpeed))
                .setSaveConsumer(value -> config().movement.horizontalSpeed = fromSlider(value))
                .setTextGetter(value -> Component.translatable("text.autoconfig.freecam.option.movement.speedValue", fromSlider(value)))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.horizontalSpeed.@Tooltip"))
                .build();

        LongSliderEntry verticalSpeed = entryBuilder.startLongSlider(
                        Component.translatable("text.autoconfig.freecam.option.movement.verticalSpeed"),
                        toSlider(config().movement.verticalSpeed),
                        toSlider(MIN_SPEED), toSlider(MAX_SPEED))
                .setDefaultValue(() -> toSlider(defaults().movement.verticalSpeed))
                .setSaveConsumer(value -> config().movement.verticalSpeed = fromSlider(value))
                .setTextGetter(value -> Component.translatable("text.autoconfig.freecam.option.movement.speedValue", fromSlider(value)))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.movement.verticalSpeed.@Tooltip"))
                .build();

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
    private SubCategoryListEntry collisionCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.collision"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.@Tooltip"));

        BooleanListEntry ignoreAll = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll"),
                        config().collision.ignoreAll)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreAll.@Tooltip[1]")
                )
                .setDefaultValue(defaults().collision.ignoreAll)
                .setSaveConsumer(value -> config().collision.ignoreAll = value)
                .build();

        BooleanListEntry ignoreTransparent = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreTransparent"),
                        config().collision.ignoreTransparent)
                //? if cloth_dependencies
                .setRequirement(() -> !ignoreAll.getValue())
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreTransparent.@Tooltip"))
                .setDefaultValue(defaults().collision.ignoreTransparent)
                .setSaveConsumer(value -> config().collision.ignoreTransparent = value)
                .build();

        BooleanListEntry ignoreOpenable = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreOpenable"),
                        config().collision.ignoreOpenable)
                //? if cloth_dependencies
                .setRequirement(() -> !ignoreAll.getValue())
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreOpenable.@Tooltip"))
                .setDefaultValue(defaults().collision.ignoreOpenable)
                .setSaveConsumer(value -> config().collision.ignoreOpenable = value)
                .build();

        BooleanListEntry ignoreCustom = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom"),
                        config().collision.ignoreCustom)
                //? if cloth_dependencies
                .setRequirement(() -> !ignoreAll.getValue())
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.collision.ignoreCustom.@Tooltip"))
                .setDefaultValue(defaults().collision.ignoreCustom)
                .setSaveConsumer(value -> config().collision.ignoreCustom = value)
                .build();

        StringListListEntry idWhitelist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids"),
                        config().collision.whitelist.ids)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.ids.@Tooltip[1]"))
                //? if cloth_dependencies
                .setDisplayRequirement(ignoreCustom::getValue)
                .setDefaultValue(defaults().collision.whitelist.ids)
                .setSaveConsumer(value -> config().collision.whitelist.ids = value)
                .build();

        StringListListEntry patternWhitelist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns"),
                        config().collision.whitelist.patterns)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.whitelist.patterns.@Tooltip[1]"))
                //? if cloth_dependencies
                .setDisplayRequirement(ignoreCustom::getValue)
                .setDefaultValue(defaults().collision.whitelist.patterns)
                .setSaveConsumer(value -> config().collision.whitelist.patterns = value)
                .build();

        BooleanListEntry alwaysCheck = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck"),
                        config().collision.alwaysCheck)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.collision.alwaysCheck.@Tooltip[1]"))
                .setDefaultValue(defaults().collision.alwaysCheck)
                .setSaveConsumer(value -> config().collision.alwaysCheck = value)
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
    private SubCategoryListEntry visualCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.visual"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.@Tooltip"));

        EnumListEntry<Perspective> perspective = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.visual.perspective"),
                        Perspective.class,
                        config().visual.perspective)
                .setEnumNameProvider(value -> {
                    String key = switch ((Perspective) value) {
                        case FIRST_PERSON -> "firstPerson";
                        case THIRD_PERSON -> "thirdPerson";
                        case THIRD_PERSON_MIRROR -> "thirdPersonMirror";
                        case INSIDE -> "inside";
                    };
                    return Component.translatable("text.autoconfig.freecam.option.visual.perspective." + key);
                })
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.perspective.@Tooltip"))
                .setDefaultValue(defaults().visual.perspective)
                .setSaveConsumer(value -> config().visual.perspective = value)
                .build();

        BooleanListEntry showPlayer = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.showPlayer"),
                        config().visual.showPlayer)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showPlayer.@Tooltip"))
                .setDefaultValue(defaults().visual.showPlayer)
                .setSaveConsumer(value -> config().visual.showPlayer = value)
                .build();

        BooleanListEntry showHand = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.showHand"),
                        config().visual.showHand)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showHand.@Tooltip"))
                .setDefaultValue(defaults().visual.showHand)
                .setSaveConsumer(value -> config().visual.showHand = value)
                .build();

        BooleanListEntry fullBright = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.fullBright"),
                        config().visual.fullBright)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.fullBright.@Tooltip"))
                .setDefaultValue(defaults().visual.fullBright)
                .setSaveConsumer(value -> config().visual.fullBright = value)
                .build();

        BooleanListEntry showSubmersion = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.visual.showSubmersion"),
                        config().visual.showSubmersion)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.visual.showSubmersion.@Tooltip"))
                .setDefaultValue(defaults().visual.showSubmersion)
                .setSaveConsumer(value -> config().visual.showSubmersion = value)
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
    private SubCategoryListEntry utilityCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.utility"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.@Tooltip"));

        BooleanListEntry disableOnDamage = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.utility.disableOnDamage"),
                        config().utility.disableOnDamage)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.disableOnDamage.@Tooltip"))
                .setDefaultValue(defaults().utility.disableOnDamage)
                .setSaveConsumer(value -> config().utility.disableOnDamage = value)
                .build();

        BooleanListEntry freezePlayer = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer"),
                        config().utility.freezePlayer)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.utility.freezePlayer.@Tooltip[1]")
                )
                .setDefaultValue(defaults().utility.freezePlayer)
                .setSaveConsumer(value -> config().utility.freezePlayer = value)
                .build();

        BooleanListEntry allowInteract = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.utility.allowInteract"),
                        config().utility.allowInteract)
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.utility.allowInteract.@Tooltip[1]")
                )
                .setDefaultValue(defaults().utility.allowInteract)
                .setSaveConsumer(value -> config().utility.allowInteract = value)
                .build();

        EnumListEntry<ModConfigDTO.InteractionMode> interactionMode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.utility.interactionMode"),
                        ModConfigDTO.InteractionMode.class,
                        config().utility.interactionMode)
                .setEnumNameProvider(value -> Component.translatable("text.autoconfig.freecam.option.utility.interactionMode." + value.name().toLowerCase()))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.utility.interactionMode.@Tooltip"))
                .setDefaultValue(defaults().utility.interactionMode)
                .setSaveConsumer(value -> config().utility.interactionMode = value)
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
    private SubCategoryListEntry serversCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.servers"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.servers.@Tooltip"));

        EnumListEntry<ModConfigDTO.ServerRestriction> mode = entryBuilder.startEnumSelector(
                        Component.translatable("text.autoconfig.freecam.option.servers.mode"),
                        ModConfigDTO.ServerRestriction.class,
                        config().servers.mode)
                .setEnumNameProvider(value -> Component.translatable("text.autoconfig.freecam.option.servers.mode." + value.name().toLowerCase()))
                .setTooltip(
                        Component.translatable("text.autoconfig.freecam.option.servers.mode.@Tooltip[0]"),
                        Component.translatable("text.autoconfig.freecam.option.servers.mode.@Tooltip[1]"))
                .setDefaultValue(defaults().servers.mode)
                .setSaveConsumer(value -> config().servers.mode = value)
                .build();

        StringListListEntry whitelist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.servers.whitelist"),
                        config().servers.whitelist)
                //? if cloth_dependencies
                .setDisplayRequirement(() -> mode.getValue() == ModConfigDTO.ServerRestriction.WHITELIST)
                .setDefaultValue(defaults().servers.whitelist)
                .setSaveConsumer(value -> config().servers.whitelist = value)
                .build();

        StringListListEntry blacklist = entryBuilder.startStrList(
                        Component.translatable("text.autoconfig.freecam.option.servers.blacklist"),
                        config().servers.blacklist)
                //? if cloth_dependencies
                .setDisplayRequirement(() -> mode.getValue() == ModConfigDTO.ServerRestriction.BLACKLIST)
                .setDefaultValue(defaults().servers.blacklist)
                .setSaveConsumer(value -> config().servers.blacklist = value)
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
    private SubCategoryListEntry notificationCategory(ConfigEntryBuilder entryBuilder) {
        SubCategoryBuilder builder = entryBuilder.startSubCategory(Component.translatable("text.autoconfig.freecam.option.notification"))
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.@Tooltip"));

        BooleanListEntry notifyFreecam = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.notification.notifyFreecam"),
                        config().notification.notifyFreecam)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.notifyFreecam.@Tooltip"))
                .setDefaultValue(defaults().notification.notifyFreecam)
                .setSaveConsumer(value -> config().notification.notifyFreecam = value)
                .build();

        BooleanListEntry notifyTripod = entryBuilder.startBooleanToggle(
                        Component.translatable("text.autoconfig.freecam.option.notification.notifyTripod"),
                        config().notification.notifyTripod)
                .setTooltip(Component.translatable("text.autoconfig.freecam.option.notification.notifyTripod.@Tooltip"))
                .setDefaultValue(defaults().notification.notifyTripod)
                .setSaveConsumer(value -> config().notification.notifyTripod = value)
                .build();

        // Add entries to the sub-category
        Stream.of(
                notifyFreecam,
                notifyTripod
        ).forEach(builder::add);

        return builder.build();
    }

    private static long toSlider(double value) {
        return (long) (value * SLIDER_SCALE);
    }

    private static double fromSlider(long value) {
        return value / SLIDER_SCALE;
    }
}
