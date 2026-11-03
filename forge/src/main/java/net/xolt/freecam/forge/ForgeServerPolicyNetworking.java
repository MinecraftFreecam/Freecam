package net.xolt.freecam.forge;

import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.xolt.freecam.network.AntiFreecamPayload;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicy;
import net.xolt.freecam.network.ServerPolicyPayload;
import java.nio.charset.StandardCharsets;

//~ if <1.18 'net.minecraftforge.network' -> 'net.minecraftforge.fmllegacy.network' {
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
//~ }

public final class ForgeServerPolicyNetworking {
    private static EventNetworkChannel policyChannel;

    public static void register() {
        policyChannel = NetworkRegistry.newEventChannel(
                ServerPolicyPayload.CHANNEL_ID, () -> "1", version -> true, version -> true
        );
        policyChannel.addListener(ForgeServerPolicyNetworking::handlePolicy);
        EventNetworkChannel antiFreecam = NetworkRegistry.newEventChannel(
                AntiFreecamPayload.CHANNEL_ID, () -> "1", version -> true, version -> true);
        antiFreecam.addListener(ForgeServerPolicyNetworking::handleAntiFreecam);
    }

    private static void handlePolicy(NetworkEvent.ClientCustomPayloadEvent event) {
        receive(event, false);
    }

    private static void handleAntiFreecam(NetworkEvent.ClientCustomPayloadEvent event) {
        receive(event, true);
    }

    private static void receive(NetworkEvent.ClientCustomPayloadEvent event, boolean antiFreecam) {
        FriendlyByteBuf buffer = event.getPayload();
        if (buffer == null) {
            return;
        }
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        NetworkEvent.Context context = event.getSource().get();
        context.enqueueWork(() -> {
            if (antiFreecam) ServerPolicies.get().applyAntiFreecamBytes(bytes);
            else ServerPolicies.get().applyBytes(bytes);
        });
        context.setPacketHandled(true);
    }

    public static boolean send(ServerPlayer player, ServerPolicy policy) {
        if (policyChannel.isRemotePresent(player.connection.connection)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(policy.toJson().getBytes(StandardCharsets.UTF_8)));
            player.connection.send(new ClientboundCustomPayloadPacket(ServerPolicyPayload.CHANNEL_ID, buffer));
            return true;
        }
        return false;
    }
}
