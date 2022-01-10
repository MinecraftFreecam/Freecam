package net.xolt.freecam.mixins;

import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "wouldPoseNotCollide", at = @At("HEAD"), cancellable = true)
    private void onWouldPoseNotCollide(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }
}
