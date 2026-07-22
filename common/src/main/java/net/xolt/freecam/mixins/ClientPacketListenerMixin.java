package net.xolt.freecam.mixins;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.20.6 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? } else {
/*import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
*///? }

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    // Disables freecam when the player respawns/switches dimensions.
    @Inject(method = "handleRespawn", at = @At("TAIL"))
    private void onPlayerRespawn(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            Freecam.toggle();
        }
    }

    //? if >=1.20.6 {
    @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V", at = @At("HEAD"))
    private void onCustomPayload(CustomPacketPayload payload, CallbackInfo ci) {
        freecam$handleCustomPayload(payload.type().id().toString());
    }
    //? } else {
    /*@Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void onCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        freecam$handleCustomPayload(packet.getIdentifier().toString());
    }
    *///? }

    @Unique
    private static void freecam$handleCustomPayload(String channel) {
        if (Freecam.SERVER_DISABLE_CHANNEL.equals(channel)) {
            Freecam.disableOnServer();
        }
    }
}
