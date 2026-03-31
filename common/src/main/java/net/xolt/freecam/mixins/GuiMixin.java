package net.xolt.freecam.mixins;

import net.minecraft.resources.Identifier;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=1.20.6 {
import net.minecraft.client.gui.GuiGraphics;
 //? } else if > 1.18.2 {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///? }

import static net.xolt.freecam.Freecam.MC;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;

@Mixin(Gui.class)
public class GuiMixin {
    // Makes HUD correspond to the player rather than the FreeCamera.
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(MC.player);
        }
    }

    // Don't render equipped-item overlays while Freecam is active
    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderTextureOverlay(
            //? if >=1.20.6 {
            GuiGraphics guiGraphics,
            //? } else if > 1.18.2
            //PoseStack poseStack,
            Identifier shaderIdentifier,
            float alpha,
            CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
