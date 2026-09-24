package net.xolt.freecam.network;

import net.xolt.freecam.config.model.ModConfigDTO;

/** Publishes immutable client configuration snapshots to the integrated server thread. */
public final class HostedServerPolicy {
    private static volatile ServerPolicy policy = ServerPolicy.ALLOW_ALL;
    private static volatile ServerPolicy hostPolicy = ServerPolicy.ALLOW_ALL;

    private HostedServerPolicy() {}

    public static void configure(ModConfigDTO config) {
        ServerPolicy configured = ServerPolicy.create(config.serverPolicy);
        boolean applyToHost = config.serverPolicy != null && config.serverPolicy.applyToHost;
        policy = configured;
        hostPolicy = applyToHost ? configured : ServerPolicy.ALLOW_ALL;
    }

    public static ServerPolicy get() {
        return policy;
    }

    /** The policy for the local player while hosting, whether or not the world is open to LAN. */
    public static ServerPolicy forHost() {
        return hostPolicy;
    }
}
