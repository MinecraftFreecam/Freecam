package net.xolt.freecam.forge;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.xolt.freecam.network.AntiFreecamPayload;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicy;
import net.xolt.freecam.network.ServerPolicyPayload;

@EventBusSubscriber(modid = "freecam"
        //? neoforge: <21
        //, bus = EventBusSubscriber.Bus.MOD
)
public class NeoforgeServerPolicyNetworking {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        //? if >=1.21.11 {
        registrar.playToClient(ServerPolicyPayload.TYPE, ServerPolicyPayload.STREAM_CODEC);
        registrar.commonToClient(AntiFreecamPayload.TYPE, AntiFreecamPayload.STREAM_CODEC);
        //? } else {
        /*registrar.playToClient(ServerPolicyPayload.TYPE, ServerPolicyPayload.STREAM_CODEC,
                (payload, context) -> ServerPolicies.get().applyJson(payload.json()));
        registrar.commonToClient(AntiFreecamPayload.TYPE, AntiFreecamPayload.STREAM_CODEC,
                (payload, context) -> ServerPolicies.get().applyAntiFreecam(payload.forceCollision()));
        *///? }
    }

    public static boolean send(ServerPlayer player, ServerPolicy policy) {
        if (!player.connection.hasChannel(ServerPolicyPayload.TYPE)) return false;
        PacketDistributor.sendToPlayer(player, new ServerPolicyPayload(policy.toJson()));
        return true;
    }
}
