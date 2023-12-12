package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import static net.xolt.freecam.BuildConfig.*;

@Config(name = "freecam")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
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

        @ConfigEntry.Gui.Tooltip(count = IGNORE_ALL_COLLISION_TOOLTIP_COUNT)
        public boolean ignoreAll = IGNORE_ALL_COLLISION_DEFAULT;

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

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean freezePlayer = false;

        @ConfigEntry.Gui.Tooltip(count = ALLOW_INTERACT_TOOLTIP_COUNT)
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
