//? if >=26.3 {
package net.xolt.freecam.mixins;

import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.util.Mth;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Moved from [ItemInHandRendererMixin] in 26.3
@Mixin(FirstPersonHandsAndItems.class)
public class FirstPersonHandsAndItemsMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(LocalPlayer player, float partialTicks, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            FreeCamera camera = Freecam.getFreeCamera();
            state.viewXRot = camera.getViewXRot(partialTicks);
            state.viewYRot = camera.getViewYRot(partialTicks);
            state.xBob = Mth.lerp(partialTicks, camera.xBobO, camera.xBob);
            state.yBob = Mth.lerp(partialTicks, camera.yBobO, camera.yBob);
        }
    }
}
//? }
