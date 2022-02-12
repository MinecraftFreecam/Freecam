package net.xolt.freecam.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "getRenderDistance", at = @At("HEAD"), cancellable = true)
    private void onGetRenderDistance(CallbackInfoReturnable<Double> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(1000D);
        }
    }
}
