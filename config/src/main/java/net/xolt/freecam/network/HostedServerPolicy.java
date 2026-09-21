package net.xolt.freecam.network;

import net.xolt.freecam.config.model.ModConfigDTO;

/** Publishes immutable client configuration snapshots to the integrated server thread. */
public final class HostedServerPolicy {
    private static volatile ServerPolicy policy = ServerPolicy.ALLOW_ALL;

    private HostedServerPolicy() {}

    public static void configure(ModConfigDTO config) {
        policy = config.serverPolicy == null ? ServerPolicy.ALLOW_ALL : config.serverPolicy.snapshot();
    }

    public static ServerPolicy get() {
        return policy;
    }
}
