package net.xolt.freecam.mixins;

//? if >=26.2 {

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;


@Mixin(Hud.class)
public class HudMixin {
    // Makes HUD correspond to the player rather than the FreeCamera.
    // NOTE: Was in GuiMixin before 26.2
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(MC.player);
        }
    }

    // Don't render equipped-item overlays while Freecam is active
    // NOTE: Was in GuiMixin before 26.2
    @Inject(method = "extractTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderTextureOverlay(
            GuiGraphicsExtractor graphics,
            Identifier texture,
            float alpha,
            CallbackInfo ci)
    {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
//? }
