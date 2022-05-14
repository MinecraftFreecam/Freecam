package net.xolt.freecam.mixins;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {

    // Makes BlockEntities render regardless of distance.
    @Inject(method = "isInRenderDistance", at = @At("HEAD"), cancellable = true)
    private void onIsInRenderDistance(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
