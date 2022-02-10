package net.xolt.freecam.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    private void onGetBoundingBox(CallbackInfoReturnable<Box> cir) {
        if (this.equals(Freecam.getFreeCamera())) {
            cir.setReturnValue(new Box(0D, 0D, 0D, 0D, 0D, 0D));
        }
    }
}
