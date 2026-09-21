package net.xolt.freecam.forge;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.config.gui.ConfigScreenProvider;
import net.xolt.freecam.network.AntiFreecamPayload;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicyPayload;

//? if >=1.21.11
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(
        //? neoforge: <21
        //bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
@SuppressWarnings("unused")
public class FreecamNeoforgeClient {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModConfig.setup();
        ModContainer container = ModList.get().getModContainerById(Freecam.MOD_ID).orElseThrow();
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (_container, parent) -> ConfigScreenProvider.provider().getConfigScreen(parent));
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        ModBindings.forEach(event::register);
    }

    //? if >=1.21.11 {
    @SubscribeEvent
    public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(ServerPolicyPayload.TYPE, (payload, context) -> ServerPolicies.applyJson(payload.json()));
        event.register(AntiFreecamPayload.TYPE, (payload, context) -> ServerPolicies.applyAntiFreecam(payload.forceCollision()));
    }
    //? }

    //? neoforge: <21 {
    /*@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GlobalEventHandler {
    *///? }
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void preTick(ClientTickEvent.Pre event) {
            Freecam.preTick(Minecraft.getInstance());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void postTick(ClientTickEvent.Post event) {
            Freecam.postTick(Minecraft.getInstance());
        }
    //? neoforge: <21
    //}
}
