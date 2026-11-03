package net.xolt.freecam.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.xolt.freecam.network.AntiFreecamPayload;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicyPayload;

//? if >=1.20.5 {
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
//? }

public final class FabricServerPolicyNetworking {

    public static void register() {
        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(
                ServerPolicyPayload.TYPE,
                (payload, context) -> ServerPolicies.get().applyJson(payload.json())
        );
        ClientConfigurationNetworking.registerGlobalReceiver(AntiFreecamPayload.TYPE,
                (payload, context) -> ServerPolicies.get().applyAntiFreecam(payload.forceCollision()));
        ClientPlayNetworking.registerGlobalReceiver(AntiFreecamPayload.TYPE,
                (payload, context) -> ServerPolicies.get().applyAntiFreecam(payload.forceCollision()));
        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> ServerPolicies.get().reset());
        //? } else {
        /*ClientPlayNetworking.registerGlobalReceiver(ServerPolicyPayload.CHANNEL_ID,
                (client, handler, buffer, responseSender) -> {
                    byte[] bytes = new byte[buffer.readableBytes()];
                    buffer.readBytes(bytes);
                    client.execute(() -> ServerPolicies.get().applyBytes(bytes));
                });
        ClientPlayNetworking.registerGlobalReceiver(AntiFreecamPayload.CHANNEL_ID,
                (client, handler, buffer, responseSender) -> {
                    byte[] bytes = new byte[buffer.readableBytes()];
                    buffer.readBytes(bytes);
                    client.execute(() -> ServerPolicies.get().applyAntiFreecamBytes(bytes));
                });
        *///? }
    }
}
