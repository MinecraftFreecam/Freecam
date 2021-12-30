package net.xolt.freecam.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            if ((packet instanceof PlayerMoveC2SPacket || packet instanceof PlayerInputC2SPacket)) {
                ci.cancel();
            } else if (packet instanceof ClientCommandC2SPacket && !((ClientCommandC2SPacket) packet).getMode().equals(ClientCommandC2SPacket.Mode.OPEN_INVENTORY)) {
                ci.cancel();
            } else if (ModConfig.INSTANCE.showClone && Freecam.getClone() != null) {
                if (packet instanceof UpdateSelectedSlotC2SPacket) {
                    Freecam.getClone().getInventory().selectedSlot = ((UpdateSelectedSlotC2SPacket) packet).getSelectedSlot();
                } else if (packet instanceof HandSwingC2SPacket) {
                    Freecam.getClone().swingHand(((HandSwingC2SPacket) packet).getHand());
                }
            }
        }
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            if (packet instanceof EntityVelocityUpdateS2CPacket) {
                if (((EntityVelocityUpdateS2CPacket) packet).getId() == MC.player.getId()) {
                    ci.cancel();
                }
            } else if (packet instanceof PlayerPositionLookS2CPacket) {
                Freecam.updatePositionLook((PlayerPositionLookS2CPacket) packet);
                ci.cancel();
            } else if (packet instanceof PlayerRespawnS2CPacket) {
                Freecam.toggle();
            }
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            Freecam.toggle();
        }
    }
}
