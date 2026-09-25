package net.xolt.freecam.network;

import com.google.gson.Gson;
import net.xolt.freecam.config.model.ServerPolicyConfig;
import org.jetbrains.annotations.Nullable;

/** Immutable policy snapshot shared by the sender and receiver. */
public record ServerPolicy(boolean allowFreecam, CollisionPolicy collision, boolean allowFullbright, boolean allowCameraInteractions) {
    public static final ServerPolicy ALLOW_ALL = new ServerPolicy(true, new CollisionPolicy(true), true, true);
    private static final Gson GSON = new Gson();

    public record CollisionPolicy(boolean allowIgnoring) {}

    /** Missing sections allow their features, matching omitted fields in received policies. */
    public static ServerPolicy create(@Nullable ServerPolicyConfig config) {
        if (config == null) return ALLOW_ALL;
        boolean allowIgnoringCollision = config.collision == null || config.collision.allowIgnoring;
        return new ServerPolicy(config.allowFreecam, new CollisionPolicy(allowIgnoringCollision),
                config.allowFullbright, config.allowCameraInteractions);
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
