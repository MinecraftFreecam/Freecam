package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.LightTexture;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @WrapOperation(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float onSetBrightnessFactor(Double instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            return Float.MAX_VALUE;
        }
        return original.call(instance);
    }
}
