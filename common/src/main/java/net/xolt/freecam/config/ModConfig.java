package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.variant.api.BuildVariant;

@Config(name = "freecam")
public class ModConfig implements ConfigData {

    private static ConfigHolder<ModConfig> CONFIG_HOLDER;

    public static void init() {
        CONFIG_HOLDER = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        ConfigExtensions.init(AutoConfig.getGuiRegistry(ModConfig.class));
    }

    public static ModConfig get() {
        return CONFIG_HOLDER.get();
    }

    public static void save() {
        CONFIG_HOLDER.save();
    }

    public static Screen getScreen(Screen parent) {
        return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public MovementConfig movement = new MovementConfig();
    public static class MovementConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public FlightMode flightMode = FlightMode.DEFAULT;

        @ConfigEntry.Gui.Tooltip
        public double horizontalSpeed = 1.0;

        @ConfigEntry.Gui.Tooltip
        public double verticalSpeed = 1.0;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public CollisionConfig collision = new CollisionConfig();
    public static class CollisionConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean ignoreTransparent = true;

        @ConfigEntry.Gui.Tooltip
        public boolean ignoreOpenable = true;

        @VariantTooltip(variant = "normal", count = 2)
        @VariantTooltip(variant = "modrinth", count = 3)
        // Default to true, when not running a modrinth build
        public boolean ignoreAll = !BuildVariant.getInstance().name().equals("modrinth");

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean alwaysCheck = false;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public VisualConfig visual = new VisualConfig();
    public static class VisualConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public Perspective perspective = Perspective.INSIDE;

        @ConfigEntry.Gui.Tooltip
        public boolean showPlayer = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showHand = false;

        @ConfigEntry.Gui.Tooltip
        public boolean fullBright = false;

        @ConfigEntry.Gui.Tooltip
        public boolean showSubmersion = false;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public UtilityConfig utility = new UtilityConfig();
    public static class UtilityConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean disableOnDamage = true;

        @VariantTooltip(count = 2)
        public boolean freezePlayer = false;

        @VariantTooltip(variant = "normal", count = 2)
        @VariantTooltip(variant = "modrinth", count = 3)
        public boolean allowInteract = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public InteractionMode interactionMode = InteractionMode.CAMERA;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public NotificationConfig notification = new NotificationConfig();
    public static class NotificationConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean notifyFreecam = true;

        @ConfigEntry.Gui.Tooltip
        public boolean notifyTripod = true;
    }

    public enum FlightMode implements SelectionListEntry.Translatable {
        CREATIVE("text.autoconfig.freecam.option.movement.flightMode.creative"),
        DEFAULT("text.autoconfig.freecam.option.movement.flightMode.default");

        private final String name;

        FlightMode(String name) {
            this.name = name;
        }

        public String getKey() {
            return name;
        }
    }

    public enum InteractionMode implements SelectionListEntry.Translatable {
        CAMERA("text.autoconfig.freecam.option.utility.interactionMode.camera"),
        PLAYER("text.autoconfig.freecam.option.utility.interactionMode.player");

        private final String name;

        InteractionMode(String name) {
            this.name = name;
        }

        public String getKey() {
            return name;
        }
    }

    public enum Perspective implements SelectionListEntry.Translatable {
        FIRST_PERSON("text.autoconfig.freecam.option.visual.perspective.firstPerson"),
        THIRD_PERSON("text.autoconfig.freecam.option.visual.perspective.thirdPerson"),
        THIRD_PERSON_MIRROR("text.autoconfig.freecam.option.visual.perspective.thirdPersonMirror"),
        INSIDE("text.autoconfig.freecam.option.visual.perspective.inside");

        private final String name;

        Perspective(String name) {
            this.name = name;
        }

        public String getKey() {
            return name;
        }
    }
}
