//? if >=1.20.5 {
package net.xolt.freecam.forge;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.network.ChannelBuilder;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.network.ServerPolicies;
import net.xolt.freecam.network.ServerPolicyPayload;

import java.nio.charset.StandardCharsets;

public final class ForgeServerPolicyNetworking {
    private static final StreamCodec<RegistryFriendlyByteBuf, ServerPolicyPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerPolicyPayload decode(RegistryFriendlyByteBuf buffer) {
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            return new ServerPolicyPayload(new String(bytes, StandardCharsets.UTF_8));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ServerPolicyPayload payload) {
            buffer.writeBytes(payload.json().getBytes(StandardCharsets.UTF_8));
        }
    };

    public static void register() {
        ChannelBuilder.named(Freecam.MOD_ID + ":server_policies")
                .networkProtocolVersion(1)
                .optional()
                .payloadChannel()
                .play()
                .clientbound()
                .addMain(
                        ServerPolicyPayload.TYPE,
                        STREAM_CODEC,
                        (payload, context) -> ServerPolicies.applyJson(payload.json())
                )
                .build();
    }
}
//? }
