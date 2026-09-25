package net.xolt.freecam.forge;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.xolt.freecam.network.ServerPolicyManager;
//? if >=1.18 {
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
//? } else {
/*import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
*///? }

@Mod.EventBusSubscriber(modid = "freecam", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeServerEvents {
    @SubscribeEvent
    //~ if <1.18 ServerStartedEvent -> FMLServerStartedEvent
    public static void start(ServerStartedEvent event) {
        ServerPolicyManager.start(event.getServer(), FMLPaths.CONFIGDIR.get());
    }

    @SubscribeEvent
    //~ if <1.18 ServerStoppedEvent -> FMLServerStoppedEvent
    public static void stop(ServerStoppedEvent event) {
        ServerPolicyManager.stop(event.getServer());
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerPolicyManager.tick(ServerLifecycleHooks.getCurrentServer(), ForgeServerPolicyNetworking::send);
        }
    }
}
