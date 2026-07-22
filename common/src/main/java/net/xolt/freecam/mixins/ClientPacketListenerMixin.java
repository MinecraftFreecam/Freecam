package net.xolt.freecam.mixins;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <1.20.5 {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.xolt.freecam.network.ServerPolicies;
*///? }

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    // Disables freecam when the player respawns/switches dimensions.
    @Inject(method = "handleRespawn", at = @At("TAIL"))
    private void onPlayerRespawn(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            Freecam.disable();
        }
    }

    //? if <1.20.5 {
    /*@Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void onCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (!ServerPolicies.CHANNEL.equals(packet.getIdentifier().toString())) {
            return;
        }

        FriendlyByteBuf data = packet.getData();
        byte[] bytes = new byte[data.readableBytes()];
        data.getBytes(data.readerIndex(), bytes);
        ServerPolicies.applyBytes(bytes);
    }
    *///? }
}
