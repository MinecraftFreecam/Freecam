//? if >=1.20.5 {
package net.xolt.freecam.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Wire-compatible with Kesuaheli/AntiFreecam's configuration-phase boolean payload. */
public record AntiFreecamPayload(boolean forceCollision) implements CustomPacketPayload {
    public static final Type<AntiFreecamPayload> TYPE = new Type<>(identifier());
    public static final StreamCodec<ByteBuf, AntiFreecamPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AntiFreecamPayload decode(ByteBuf buffer) {
            return new AntiFreecamPayload(buffer.readBoolean());
        }

        @Override
        public void encode(ByteBuf buffer, AntiFreecamPayload payload) {
            buffer.writeBoolean(payload.forceCollision());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static Identifier identifier() {
        //? if >=1.21 {
        return Identifier.fromNamespaceAndPath("antifreecam", "freecam_config_packet");
        //? } else
        //return new ResourceLocation(ServerPolicies.ANTI_FREECAM_CHANNEL);
    }
}
//? }
