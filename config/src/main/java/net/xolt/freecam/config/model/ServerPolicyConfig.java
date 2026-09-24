package net.xolt.freecam.config.model;

/** Settings for servers hosted by this installation, independent of received restrictions. */
public class ServerPolicyConfig {
    public boolean allowFreecam = true;
    public CollisionPolicyConfig collision = new CollisionPolicyConfig();
    public static class CollisionPolicyConfig {
        public boolean allowIgnoring = true;
    }

    public boolean allowFullbright = true;
    public boolean allowInteract = true;

    /** Whether the host of an integrated server is restricted too; ignored by dedicated servers. */
    public boolean applyToHost = false;
}
