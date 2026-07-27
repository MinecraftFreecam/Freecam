package net.xolt.freecam.forge;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.config.gui.ConfigScreenProvider;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicyPayload;

//? if >=1.21.11
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;


@Mod(value = Freecam.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(
        //? neoforge: <21
        //bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
@SuppressWarnings("unused")
public class FreecamNeoforge {

    public FreecamNeoforge(ModContainer container) {
        // Register our config screen with Forge
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (_container, parent) -> ConfigScreenProvider.provider().getConfigScreen(parent)
        );
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModConfig.setup();
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        ModBindings.forEach(event::register);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        //? if >=1.21.11 {
        registrar.playToClient(ServerPolicyPayload.TYPE, ServerPolicyPayload.STREAM_CODEC);
        //? } else {
        /*registrar.playToClient(
                ServerPolicyPayload.TYPE,
                ServerPolicyPayload.STREAM_CODEC,
                FreecamNeoforge::handleServerPolicy
        );
        *///? }
    }

    //? if >=1.21.11 {
    @SubscribeEvent
    public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(ServerPolicyPayload.TYPE, FreecamNeoforge::handleServerPolicy);
    }
    //? }

    private static void handleServerPolicy(ServerPolicyPayload payload, IPayloadContext context) {
        ServerPolicies.applyJson(payload.json());
    }

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
