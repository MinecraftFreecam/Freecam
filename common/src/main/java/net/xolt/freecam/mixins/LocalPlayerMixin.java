package net.xolt.freecam.mixins;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class LocalPlayerMixin {

    // Needed for Baritone compatibility.
    @Inject(method = "isControlledCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }

    // Makes rotation depend upon FreeCamera rather than the player.
    @Inject(method = "getViewXRot", at = @At("HEAD"), cancellable = true)
    private void onGetViewXRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !ModConfig.get().allowInteractionsFromPlayer()) {
            cir.setReturnValue(Freecam.getFreeCamera().getViewXRot(partialTick));
        }
    }

    // Makes rotation depend upon FreeCamera rather than the player.
    @Inject(method = "getViewYRot", at = @At("HEAD"), cancellable = true)
    private void onGetViewYRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !ModConfig.get().allowInteractionsFromPlayer()) {
            cir.setReturnValue(Freecam.getFreeCamera().getViewYRot(partialTick));
        }
    }
}
