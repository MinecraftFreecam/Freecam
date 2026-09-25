package net.xolt.freecam.network;

import net.xolt.freecam.config.model.ModConfigDTO;

/** Publishes immutable client configuration snapshots to the integrated server thread. */
public final class HostedServerPolicy {
    private static final HostedServerPolicy CLIENT = new HostedServerPolicy();

    private volatile ServerPolicy forClients = ServerPolicy.ALLOW_ALL;
    private volatile ServerPolicy forHost = ServerPolicy.ALLOW_ALL;

    /** The policy hosted by this client's integrated server. */
    public static HostedServerPolicy get() {
        return CLIENT;
    }

    public void configure(ModConfigDTO config) {
        ServerPolicy configured = ServerPolicy.create(config.serverPolicy);
        boolean applyToHost = config.serverPolicy != null && config.serverPolicy.applyToHost;
        forClients = configured;
        forHost = applyToHost ? configured : ServerPolicy.ALLOW_ALL;
    }

    /** The policy sent to clients connected over LAN. */
    public ServerPolicy forClients() {
        return forClients;
    }

    /** The policy for the local player while hosting, whether or not the world is open to LAN. */
    public ServerPolicy forHost() {
        return forHost;
    }
}
