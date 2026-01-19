package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.xolt.freecam.config.gui.AutoConfigExtensions;
import net.xolt.freecam.config.gui.BoundedContinuous;
import net.xolt.freecam.config.gui.ModBindingsConfig;
import net.xolt.freecam.config.gui.ValidateRegex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Config(name = "freecam")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        ConfigHolder<ModConfig> holder = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        AutoConfigExtensions.apply(ModConfig.class);
        holder.registerSaveListener(CollisionBehavior::onConfigChange);
        holder.registerLoadListener(CollisionBehavior::onConfigChange);
        INSTANCE = holder.getConfig();
        CollisionBehavior.onConfigChange(holder, INSTANCE); // Listener isn't called on initial load...
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public ControlsConfig controls = new ControlsConfig();
    public static class ControlsConfig {
        @ModBindingsConfig
        private Object keys;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public MovementConfig movement = new MovementConfig();
    public static class MovementConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public FlightMode flightMode = FlightMode.DEFAULT;

        @ConfigEntry.Gui.Tooltip
        @BoundedContinuous(max = 10)
        public double horizontalSpeed = 1.0;

        @ConfigEntry.Gui.Tooltip
        @BoundedContinuous(max = 10)
        public double verticalSpeed = 1.0;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public CollisionConfig collision = new CollisionConfig();
    public static class CollisionConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean ignoreTransparent = false;

        @ConfigEntry.Gui.Tooltip
        public boolean ignoreOpenable = false;

        @ConfigEntry.Gui.Tooltip
        public boolean ignoreCustom = false;

        @ConfigEntry.Gui.TransitiveObject
        public CollisionWhitelist whitelist = new CollisionWhitelist();
        public static class CollisionWhitelist {
            @ConfigEntry.Gui.Tooltip(count = 2)
            public List<String> ids = new ArrayList<>();
            @ValidateRegex
            @ConfigEntry.Gui.Tooltip(count = 2)
            public List<String> patterns = new ArrayList<>();
        }

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean ignoreAll = true;

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

        @ConfigEntry.Gui.Tooltip
        public boolean outlinePlayer = false;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public UtilityConfig utility = new UtilityConfig();
    public static class UtilityConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean disableOnDamage = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean freezePlayer = false;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean allowInteract = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public InteractionMode interactionMode = InteractionMode.CAMERA;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public ServerConfig servers = new ServerConfig();
    public static class ServerConfig {
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        public ServerRestriction mode = ServerRestriction.NONE;

        // These must be mutable lists, so no Collections.emptyList()
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>();
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
        CREATIVE("creative"),
        DEFAULT("default");

        private final String key;

        FlightMode(String name) {
            this.key = "text.autoconfig.freecam.option.movement.flightMode." + name;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }
    }

    public enum InteractionMode implements SelectionListEntry.Translatable {
        CAMERA("camera"),
        PLAYER("player");

        private final String key;

        InteractionMode(String name) {
            this.key = "text.autoconfig.freecam.option.utility.interactionMode." + name;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }
    }

    public enum Perspective implements SelectionListEntry.Translatable {
        FIRST_PERSON("firstPerson"),
        THIRD_PERSON("thirdPerson"),
        THIRD_PERSON_MIRROR("thirdPersonMirror"),
        INSIDE("inside");

        private final String key;

        Perspective(String name) {
            this.key = "text.autoconfig.freecam.option.visual.perspective." + name;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }
    }

    public enum ServerRestriction implements SelectionListEntry.Translatable {
        NONE, WHITELIST, BLACKLIST;

        @Override
        public @NotNull String getKey() {
            return "text.autoconfig.freecam.option.servers.mode." + toString().toLowerCase();
        }
    }
}
