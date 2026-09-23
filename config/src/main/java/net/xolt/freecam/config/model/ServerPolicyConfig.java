package net.xolt.freecam.config.model;

import net.xolt.freecam.network.ServerPolicy;

/** Settings for servers hosted by this installation, independent of received restrictions. */
public class ServerPolicyConfig {
    public boolean allowFreecam = true;
    public CollisionPolicyConfig collision = new CollisionPolicyConfig();
    public static class CollisionPolicyConfig {
        public boolean allowIgnoring = true;
    }

    public boolean allowFullbright = true;
    public boolean allowInteract = true;

    public ServerPolicy snapshot() {
        boolean allowIgnoringCollision = collision == null || collision.allowIgnoring;
        return new ServerPolicy(allowFreecam, allowIgnoringCollision, allowFullbright, allowInteract);
    }
}
