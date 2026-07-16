package net.xolt.freecam.config.model;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModConfigDTO implements RawJsonHolder {

    private transient @Nullable JsonObject rawJson;

    @Override
    public void setRawJson(@Nullable JsonObject rawJson) {
        this.rawJson = rawJson;
    }

    @Override
    public @Nullable JsonObject getRawJson() {
        return rawJson;
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
        public boolean outlinePlayer = false;
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
