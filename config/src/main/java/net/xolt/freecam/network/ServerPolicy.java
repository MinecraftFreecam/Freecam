package net.xolt.freecam.network;

import com.google.gson.Gson;

/** Immutable policy snapshot shared by the sender and receiver. */
public record ServerPolicy(boolean allowFreecam, CollisionPolicy collision, boolean allowFullbright, boolean allowInteract) {
    public static final ServerPolicy ALLOW_ALL = new ServerPolicy(true, new CollisionPolicy(true), true, true);
    private static final Gson GSON = new Gson();

    public record CollisionPolicy(boolean allowIgnoring) {}

    public String toJson() {
        return GSON.toJson(this);
    }
}
