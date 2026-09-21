package net.xolt.freecam.forge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.xolt.freecam.network.ServerPolicyManager;

@EventBusSubscriber(modid = "freecam"
        //? neoforge: <21
        //, bus = EventBusSubscriber.Bus.GAME
)
public class NeoforgeServerEvents {
    @SubscribeEvent
    public static void start(ServerStartedEvent event) {
        ServerPolicyManager.start(event.getServer(), FMLPaths.CONFIGDIR.get());
    }

    @SubscribeEvent
    public static void stop(ServerStoppedEvent event) {
        ServerPolicyManager.stop(event.getServer());
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        ServerPolicyManager.tick(event.getServer(), NeoforgeServerPolicyNetworking::send);
    }
}
