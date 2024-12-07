package net.xolt.freecam.mixins;

import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;

@Mixin(Gui.class)
public class GuiMixin {
    // Makes HUD correspond to the player rather than the FreeCamera.
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(MC.player);
        }
    }

    // Don't render pumpkin overlay while Freecam is active
    @Inject(method = "renderPumpkin", at = @At("HEAD"), cancellable = true)
    private void onRenderPumpkin(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
