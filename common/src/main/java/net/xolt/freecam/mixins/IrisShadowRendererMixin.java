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
        //? if >=1.20 {
        targets = "net.irisshaders.iris.shadows.ShadowRenderer",
        //? } else
        //targets = "net.coderbot.iris.pipeline.ShadowRenderer",
        remap = false
)
public class IrisShadowRendererMixin {

    // Hide player shadow in freecam if showPlayer is disabled
    @Inject(method = "renderPlayerEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderPlayerShadow(CallbackInfoReturnable<Integer> cir) {
        if (Freecam.isEnabled() && !ModConfig.INSTANCE.visual.showPlayer) {
            cir.setReturnValue(0);
        }
    }
}
