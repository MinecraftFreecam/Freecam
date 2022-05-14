package net.xolt.freecam.mixins;

import net.minecraft.entity.LivingEntity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // Allows for the horizontal speed of creative flight to be configured separately from vertical speed.
    @Inject(method = "getMovementSpeed(F)F", at = @At("HEAD"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.flightMode.equals(ModConfig.FlightMode.CREATIVE) && this.equals(Freecam.getFreeCamera())) {
            cir.setReturnValue((float) (ModConfig.INSTANCE.horizontalSpeed / 10) * (Freecam.getFreeCamera().isSprinting() ? 2 : 1));
        }
    }
}
