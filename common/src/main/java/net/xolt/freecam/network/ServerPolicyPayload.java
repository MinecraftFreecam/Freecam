package net.xolt.freecam.network;

import net.minecraft.resources.Identifier;

//? if >=1.20.5 {
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
//? }

//? if >=1.20.5 {
public record ServerPolicyPayload(String json) implements CustomPacketPayload {
//? } else {
/*public final class ServerPolicyPayload {
    private ServerPolicyPayload() {}
*///? }

    //~ if >=1.21 'new ResourceLocation' -> 'Identifier.parse'
    public static final Identifier CHANNEL_ID = Identifier.parse(ServerPolicies.CHANNEL);

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<ServerPolicyPayload> TYPE = new Type<>(CHANNEL_ID);
    public static final StreamCodec<ByteBuf, ServerPolicyPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ServerPolicyPayload decode(ByteBuf buffer) {
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
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //? }
}
