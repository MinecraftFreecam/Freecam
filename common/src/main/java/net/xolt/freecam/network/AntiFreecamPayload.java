package net.xolt.freecam.network;

import net.minecraft.resources.Identifier;

//? if >=1.20.5 {
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//? }

/// Wire-compatible with [AntiFreecam](https://github.com/Kesuaheli/AntiFreecam)'s
/// configuration-phase boolean payload.
///
/// Note: AntiFreecam dates back to 1.21.5, but we support it client-side on all versions.
//? if >=1.20.5 {
public record AntiFreecamPayload(boolean forceCollision) implements CustomPacketPayload {
//? } else {
/*public final class AntiFreecamPayload {
    private AntiFreecamPayload() {}
*///? }

    //~ if >=1.21 'new ResourceLocation' -> 'Identifier.parse'
    public static final Identifier CHANNEL_ID = Identifier.parse(ServerPolicies.ANTI_FREECAM_CHANNEL);

    //? if >=1.20.5 {
    public static final Type<AntiFreecamPayload> TYPE = new Type<>(CHANNEL_ID);
    public static final StreamCodec<ByteBuf, AntiFreecamPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull AntiFreecamPayload decode(ByteBuf buffer) {
            return new AntiFreecamPayload(buffer.readBoolean());
        }

        @Override
        public void encode(ByteBuf buffer, AntiFreecamPayload payload) {
            buffer.writeBoolean(payload.forceCollision());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //? }
}
