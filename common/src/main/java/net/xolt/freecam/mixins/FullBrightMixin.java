package net.xolt.freecam.mixins;

//~ if >=26.0 LightTexture -> Lightmap {
import net.minecraft.client.renderer.LightTexture;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if <26.1 && >=1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? } else {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }
//~ if >=1.19 'net.minecraft.world.level.Level' -> 'net.minecraft.world.level.dimension.DimensionType'
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(LightTexture.class)
public class FullBrightMixin {

    // FIXME: This impl didn't work on 1.21.11 - it probably won't on 26.1
    // Even though the hook still applies, actual brightness is not increased
    //? if <26.1 && >=1.21.11 {
    @WrapOperation(method = "updateLightTexture", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"))
    private float getBrightness(DimensionType instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled()) {
            return 1.0f;
        }
        return original.call(instance);
    }
    //? } else {
    /*@Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    //~ if >1.18.2 'private void' -> 'private static void'
    //~ if >1.18.2 'Level level' -> 'DimensionType dimensionType'
    private static void onGetBrightness(DimensionType dimensionType, int lightLevel, CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled()) {
            cir.setReturnValue(1.0f);
        }
    }
    *///? }
}
//~ }
