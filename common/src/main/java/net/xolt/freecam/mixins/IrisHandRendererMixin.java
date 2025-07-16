package net.xolt.freecam.mixins;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "net.irisshaders.iris.pathways.HandRenderer",
        remap = false
)
public class IrisHandRendererMixin {

    // Hide hand in freecam if showHand is disabled
    @Inject(method = "canRender", at = @At("HEAD"), cancellable = true)
    private void onRenderItemInHand(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && !ModConfig.INSTANCE.visual.showHand) {
            cir.setReturnValue(false);
        }
    }
}
