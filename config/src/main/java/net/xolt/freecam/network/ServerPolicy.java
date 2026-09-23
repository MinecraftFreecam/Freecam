package net.xolt.freecam.network;

import com.google.gson.JsonObject;

/** Immutable policy snapshot shared by the sender and receiver. */
public record ServerPolicy(boolean allowFreecam, boolean allowIgnoringCollision, boolean allowFullbright, boolean allowInteract) {
    public static final ServerPolicy ALLOW_ALL = new ServerPolicy(true, true, true, true);

    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("allowFreecam", allowFreecam);
        JsonObject collision = new JsonObject();
        collision.addProperty("allowIgnoring", allowIgnoringCollision);
        json.add("collision", collision);
        json.addProperty("allowFullbright", allowFullbright);
        json.addProperty("allowInteract", allowInteract);
        return json.toString();
    }
}
