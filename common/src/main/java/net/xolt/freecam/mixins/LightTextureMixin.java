package net.xolt.freecam.mixins;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private void onGetBrightness(Level level, int lightLevel, CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            cir.setReturnValue(1.0f);
        }
    }
}
