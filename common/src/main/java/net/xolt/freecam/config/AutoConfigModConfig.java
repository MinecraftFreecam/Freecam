package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.xolt.freecam.config.gui.*;

import java.util.ArrayList;
import java.util.List;

@Config(name = "freecam")
public class AutoConfigModConfig implements ConfigData, MCAwareModConfig {

    @ConfigEntry.Gui.Excluded
    static AutoConfigModConfig INSTANCE;

    public static void init() {
        ConfigHolder<AutoConfigModConfig> holder = AutoConfig.register(AutoConfigModConfig.class, JanksonConfigSerializer::new);
        AutoConfigExtensions.apply(AutoConfigModConfig.class);
        INSTANCE = holder.getConfig();
        holder.registerSaveListener(INSTANCE::onConfigChange);
        holder.registerLoadListener(INSTANCE::onConfigChange);
    }

    public InteractionResult onConfigChange(ConfigHolder<AutoConfigModConfig> holder, AutoConfigModConfig config) {
        collision.behavior.rebuild(config.collision);
        return InteractionResult.PASS;
    }

    @Override
    public FlightMode getFlightMode() {
        return movement.flightMode;
    }

    @Override
    public double getHorizontalSpeed() {
        return movement.horizontalSpeed;
    }

    @Override
    public double getVerticalSpeed() {
        return movement.verticalSpeed;
    }

    @Override
    public boolean ignoreAllCollision() {
        return collision.ignoreAll;
    }

    @Override
    public boolean shouldCheckInitialCollision() {
        return collision.alwaysCheck || !collision.ignoreAll;
    }

    @Override
    public boolean ignoreCollisionWith(Block block) {
        return collision.ignoreAll || collision.behavior.isIgnored(block);
    }

    @Override
    public Perspective getInitialPerspective() {
        return visual.perspective;
    }

    @Override
    public boolean shouldShowPlayer() {
        return visual.showPlayer;
    }

    @Override
    public boolean shouldShowHand() {
        return visual.showHand;
    }

    @Override
    public boolean isFullBrightEnabled() {
        return visual.fullBright;
    }

    @Override
    public boolean shouldShowSubmersionFog() {
        return visual.showSubmersion;
    }

    @Override
    public boolean shouldDisableOnDamage() {
        return utility.disableOnDamage;
    }

    @Override
    public boolean shouldFreezePlayer() {
        return utility.freezePlayer;
    }

    @Override
    public boolean shouldPreventInteractions() {
        return !utility.allowInteract;
    }

    public boolean allowInteractionsFrom(InteractionMode mode) {
        return utility.allowInteract && utility.interactionMode == mode;
    }

    @Override
    public boolean allowInteractionsFromCamera() {
        return allowInteractionsFrom(InteractionMode.CAMERA);
    }

    @Override
    public boolean allowInteractionsFromPlayer() {
        return allowInteractionsFrom(InteractionMode.PLAYER);
    }

    @Override
    public boolean isRestrictedOnServer(String serverIp) {
        return switch (servers.mode) {
            case NONE -> false;
            case WHITELIST -> {
                String ip = serverIp.trim().toLowerCase();
                yield servers.whitelist.stream()
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .noneMatch(ip::equals);
            }
            case BLACKLIST -> {
                String ip = serverIp.trim().toLowerCase();
                yield servers.blacklist.stream()
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .anyMatch(ip::equals);
            }
        };
    }

    @Override
    public boolean shouldNotifyFreecam() {
        return notification.notifyFreecam;
    }

    @Override
    public boolean shouldNotifyTripod() {
        return notification.notifyTripod;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public ControlsConfig controls = new ControlsConfig();
    public static class ControlsConfig {
        @ModBindingsConfig
        private transient Object keys;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public MovementConfig movement = new MovementConfig();
    public static class MovementConfig {
        @TranslatableEnumButton
        @ConfigEntry.Gui.Tooltip
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

        @ConfigEntry.Gui.Excluded
        private final transient CollisionBehavior behavior = new CollisionBehavior(this);
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public VisualConfig visual = new VisualConfig();
    public static class VisualConfig {
        @TranslatableEnumButton
        @ConfigEntry.Gui.Tooltip
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

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean allowInteract = false;

        @TranslatableEnumButton
        @ConfigEntry.Gui.Tooltip
        public InteractionMode interactionMode = InteractionMode.CAMERA;
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public ServerConfig servers = new ServerConfig();
    public static class ServerConfig {
        @TranslatableEnumButton
        @ConfigEntry.Gui.Tooltip(count = 2)
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

    public enum InteractionMode {
        CAMERA, PLAYER
    }

    public enum ServerRestriction {
        NONE, WHITELIST, BLACKLIST
    }
}
