package net.xolt.freecam.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.xolt.freecam.config.load.ModConfigLoader;
import net.xolt.freecam.config.load.RawJsonPreservingSerializer;
import net.xolt.freecam.config.model.ModConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/** Server-side lifecycle; must not load client-side classes. */
public final class ServerPolicyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPolicyManager.class);
    private static final Map<MinecraftServer, State> SERVERS = new IdentityHashMap<>();

    private ServerPolicyManager() {}

    public static void start(MinecraftServer server, Path configDir) {
        ServerPolicy policy = ServerPolicy.ALLOW_ALL;
        if (server.isDedicatedServer()) {
            ModConfigLoader loader = new ModConfigLoader(new RawJsonPreservingSerializer(), "freecam", configDir);
            ModConfigDTO config = null;
            try {
                config = loader.read();
                policy = ServerPolicy.create(config.serverPolicy);
            } catch (Exception e) {
                // Fail closed: a broken config must not silently lift the server's restrictions.
                LOGGER.error("Failed to load Freecam server policy; restricting all features", e);
                policy = ServerPolicy.RESTRICT_ALL;
            }
            if (config != null && !Files.exists(loader.getFilepath())) {
                try {
                    loader.write(config);
                } catch (Exception e) {
                    LOGGER.warn("Failed to write default Freecam server policy", e);
                }
            }
        }
        SERVERS.put(server, new State(policy));
        if (server.isDedicatedServer()) LOGGER.info("Loaded Freecam server policy: {}", policy);
    }

    public static void tick(MinecraftServer server, BiPredicate<ServerPlayer, ServerPolicy> send) {
        State state = SERVERS.get(server);
        if (state == null) return;
        ServerPolicy policy = server.isDedicatedServer() ? state.dedicatedPolicy
                : server.isPublished() ? HostedServerPolicy.get().forClients() : ServerPolicy.ALLOW_ALL;
        state.broadcaster.tick(server.getPlayerList().getPlayers(), policy, send);
    }

    public static void stop(MinecraftServer server) {
        SERVERS.remove(server);
    }

    private static final class State {
        private final ServerPolicy dedicatedPolicy;
        private final PolicyBroadcaster<ServerPlayer> broadcaster = new PolicyBroadcaster<>();

        private State(ServerPolicy dedicatedPolicy) {
            this.dedicatedPolicy = dedicatedPolicy;
        }
    }
}
