/**
 * {@code renderPlayerEntity} is unused since 1.21.9.
 * <p>
 * Iris now renders players as normal entities, so we hook into {@link net.minecraft.client.renderer.entity.EntityRenderDispatcher#shouldRender}
 * in {@link net.xolt.freecam.mixins.EntityRenderDispatcherMixin} instead.
 *
 * @see <a href="https://github.com/IrisShaders/Iris/commit/e61bb8124f71b6f5d555053bcb798ed2e882b18e"><code>e61bb812</code></a>
 */

//? if <1.21.9 {
/*package net.xolt.freecam.mixins;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        //~ if >=1.20 'net.coderbot.iris.pipeline' -> 'net.irisshaders.iris.shadows'
        targets = "net.irisshaders.iris.shadows.ShadowRenderer",
        remap = false
)
public class IrisShadowRendererMixin {

    // Hide player shadow in freecam if showPlayer is disabled
    @Inject(method = "renderPlayerEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderPlayerShadow(CallbackInfoReturnable<Integer> cir) {
        if (Freecam.isEnabled() && ModConfig.get().shouldHidePlayer()) {
            cir.setReturnValue(0);
        }
    }
}
*///? }
