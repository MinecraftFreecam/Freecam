//? if >=1.20.5 {
package net.xolt.freecam.network;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//~ if >=1.21.11 ResourceLocation -> Identifier
import net.minecraft.resources.Identifier;
import net.xolt.freecam.Freecam;

import java.nio.charset.StandardCharsets;

public record ServerPolicyPayload(String json) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerPolicyPayload> TYPE = new CustomPacketPayload.Type<>(identifier());
    public static final StreamCodec<ByteBuf, ServerPolicyPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerPolicyPayload decode(ByteBuf buffer) {
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            return new ServerPolicyPayload(new String(bytes, StandardCharsets.UTF_8));
        }

        @Override
        public void encode(ByteBuf buffer, ServerPolicyPayload payload) {
            byte[] bytes = payload.json().getBytes(StandardCharsets.UTF_8);
            buffer.writeBytes(bytes);
        }
    };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static Identifier identifier() {
        //? if >=1.21 {
        return Identifier.fromNamespaceAndPath(Freecam.MOD_ID, "server_config");
        //? } else
        //return new ResourceLocation(Freecam.MOD_ID, "server_config");
    }
}
//? }
