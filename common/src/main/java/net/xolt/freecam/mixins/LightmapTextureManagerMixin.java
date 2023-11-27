package net.xolt.freecam.mixins;

import net.minecraft.client.render.LightmapTextureManager;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"), index = 2)
    private int onSetColor(int color) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            return 0xFFFFFFFF;
        }
        return color;
    }
}
