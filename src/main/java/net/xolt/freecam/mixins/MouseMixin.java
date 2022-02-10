package net.xolt.freecam.mixins;

import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void onUpdateMouse(ClientPlayerEntity player, double x, double y) {
        if (Freecam.isEnabled() && Freecam.getFreeCamera() != null) {
            Freecam.getFreeCamera().changeLookDirection(x, y);
        } else {
            player.changeLookDirection(x, y);
        }
    }
}
