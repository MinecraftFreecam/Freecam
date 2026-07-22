//? if >=1.20.5 {
package net.xolt.freecam.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicyPayload;

public final class FabricServerPolicyNetworking {

    public static void register() {
        //~ if >=26.1 playS2C -> clientboundPlay
        PayloadTypeRegistry.clientboundPlay().register(
                ServerPolicyPayload.TYPE,
                ServerPolicyPayload.STREAM_CODEC
        );
        ClientPlayNetworking.registerGlobalReceiver(
                ServerPolicyPayload.TYPE,
                (payload, context) -> ServerPolicies.applyJson(payload.json())
        );
    }
}
//? }
