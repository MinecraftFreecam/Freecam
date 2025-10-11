package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @WrapOperation(method = "updateLightTexture",
            at = @At(value="INVOKE", target="Lnet/minecraft/client/renderer/LightTexture;getBrightness(Lnet/minecraft/world/level/Level;I)F"))
    private float onSetBrightnessFactor(LightTexture instance, Level level, int lightLevel, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            return 1.0f;
        }
        return original.call(instance, level, lightLevel);
    }
}
