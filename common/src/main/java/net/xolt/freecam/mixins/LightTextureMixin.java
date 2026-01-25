package net.xolt.freecam.mixins;

import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.client.renderer.LightTexture;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? } else {
/*import org.spongepowered.asm.mixin.injection.Inject;
*///? }

@Mixin(LightTexture.class)
public class LightTextureMixin {

    //? if >=1.21.11 {
    @WrapOperation(method = "updateLightTexture",
            at = @At(value="INVOKE", target="Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"))
    private float onSetBrightnessFactor(DimensionType instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            return 1.0f;
        }
        return original.call(instance);
    }
    //? } else {
    /*@Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private static void onGetBrightness(DimensionType dimensionType, int lightLevel, CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.fullBright) {
            cir.setReturnValue(1.0f);
        }
    }
    *///? }
}
