package net.xolt.freecam.mixins;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;
import static net.xolt.freecam.config.ModConfig.FlightMode.CREATIVE;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract float getHealth();

    // Allows for the horizontal speed of creative flight to be configured separately from vertical speed.
    @Inject(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.movement.flightMode.equals(CREATIVE) && this.equals(Freecam.getFreeCamera())) {
            cir.setReturnValue((float) (ModConfig.INSTANCE.movement.horizontalSpeed / 10) * (Freecam.getFreeCamera().isSprinting() ? 2 : 1));
        }
    }

    // Disables freecam upon receiving damage if disableOnDamage is enabled.
    @Inject(method = "setHealth", at = @At("HEAD"))
    private void onSetHealth(float health, CallbackInfo ci) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.utility.disableOnDamage && this.equals(MC.player)) {
            if (!MC.player.isCreative() && getHealth() > health) {
                Freecam.disableNextTick();
            }
        }
    }
}
