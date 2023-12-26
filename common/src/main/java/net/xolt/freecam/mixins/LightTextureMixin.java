package net.xolt.freecam.mixins;

import net.minecraft.client.renderer.LightTexture;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @ModifyArg(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;setPixelRGBA(III)V"), index = 2)
    private int onSetColor(int color) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            return 0xFFFFFFFF;
        }
        return color;
    }
}
