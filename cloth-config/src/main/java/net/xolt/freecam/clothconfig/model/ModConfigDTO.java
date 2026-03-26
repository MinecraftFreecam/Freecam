package net.xolt.freecam.clothconfig.model;

import net.minecraft.world.level.block.Block;
import net.xolt.freecam.config.MCAwareModConfig;
import net.xolt.freecam.config.model.FlightMode;
import net.xolt.freecam.config.model.Perspective;

import java.util.ArrayList;
import java.util.List;

// TODO: separate DTO from domain model
// move CollisionBehavior and ModConfig-impl to domain model adapter
public class ModConfigDTO implements MCAwareModConfig {

    private transient CollisionPredicate collisionPredicate;

    public ModConfigDTO() {
        onConfigChange();
    }

    public void onConfigChange() {
        collisionPredicate = CollisionPredicate.create(collision);
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
        return collision.ignoreAll || collisionPredicate.shouldIgnore(block);
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

    public ControlsConfig controls = new ControlsConfig();
    public static class ControlsConfig {
        private transient Object keys;
    }

    public MovementConfig movement = new MovementConfig();
    public static class MovementConfig {
        public FlightMode flightMode = FlightMode.DEFAULT;
        public double horizontalSpeed = 1.0;
        public double verticalSpeed = 1.0;
    }

    public CollisionConfig collision = new CollisionConfig();
    public static class CollisionConfig {
        public boolean ignoreTransparent = false;
        public boolean ignoreOpenable = false;
        public boolean ignoreCustom = false;

        public CollisionWhitelist whitelist = new CollisionWhitelist();
        public static class CollisionWhitelist {
            public List<String> ids = new ArrayList<>();
            public List<String> patterns = new ArrayList<>();
        }

        public boolean ignoreAll = true;
        public boolean alwaysCheck = false;
    }

    public VisualConfig visual = new VisualConfig();
    public static class VisualConfig {
        public Perspective perspective = Perspective.INSIDE;
        public boolean showPlayer = true;
        public boolean showHand = false;
        public boolean fullBright = false;
        public boolean showSubmersion = false;
    }

    public UtilityConfig utility = new UtilityConfig();
    public static class UtilityConfig {
        public boolean disableOnDamage = true;
        public boolean freezePlayer = false;
        public boolean allowInteract = false;
        public InteractionMode interactionMode = InteractionMode.CAMERA;
    }

    public ServerConfig servers = new ServerConfig();
    public static class ServerConfig {
        public ServerRestriction mode = ServerRestriction.NONE;

        // These must be mutable lists, so no Collections.emptyList()
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>();
    }

    public NotificationConfig notification = new NotificationConfig();
    public static class NotificationConfig {
        public boolean notifyFreecam = true;
        public boolean notifyTripod = true;
    }

    public enum InteractionMode {
        CAMERA, PLAYER
    }

    public enum ServerRestriction {
        NONE, WHITELIST, BLACKLIST
    }
}
