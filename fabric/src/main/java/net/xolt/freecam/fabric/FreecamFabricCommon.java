package net.xolt.freecam.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.xolt.freecam.network.ServerPolicy;
import net.xolt.freecam.network.ServerPolicyManager;
//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.xolt.freecam.network.AntiFreecamPayload;
import net.xolt.freecam.network.ServerPolicyPayload;
//? } else {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.xolt.freecam.network.ServerPolicies;
import java.nio.charset.StandardCharsets;
*///? }

public class FreecamFabricCommon implements ModInitializer {
    @Override
    public void onInitialize() {
        //? if >=1.20.5 {
        //~ if >=26.1 playS2C -> clientboundPlay {
        PayloadTypeRegistry.clientboundPlay().register(ServerPolicyPayload.TYPE, ServerPolicyPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AntiFreecamPayload.TYPE, AntiFreecamPayload.STREAM_CODEC);
        //~ }
        //~ if >=26.1 configurationS2C -> clientboundConfiguration
        PayloadTypeRegistry.clientboundConfiguration().register(AntiFreecamPayload.TYPE, AntiFreecamPayload.STREAM_CODEC);
        //? }
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                ServerPolicyManager.start(server, FabricLoader.getInstance().getConfigDir()));
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerPolicyManager::stop);
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerPolicyManager.tick(server, FreecamFabricCommon::send));
    }

    private static boolean send(ServerPlayer player, ServerPolicy policy) {
        //? if >=1.20.5 {
        if (!ServerPlayNetworking.canSend(player, ServerPolicyPayload.TYPE)) return false;
        ServerPlayNetworking.send(player, new ServerPolicyPayload(policy.toJson()));
        //? } else {
        /*Identifier channel = new Identifier(ServerPolicies.CHANNEL);
        if (!ServerPlayNetworking.canSend(player, channel)) return false;
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeBytes(policy.toJson().getBytes(StandardCharsets.UTF_8));
        ServerPlayNetworking.send(player, channel, buffer);
        *///? }
        return true;
    }
}
