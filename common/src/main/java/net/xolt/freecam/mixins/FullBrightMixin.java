package net.xolt.freecam.mixins;

//~ if >=26.0 LightTexture -> Lightmap {
import net.minecraft.client.renderer.LightTexture;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >= 1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? } else {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }
//~ if >=26.0 'net.minecraft.world.level.dimension.DimensionType' -> 'net.minecraft.client.renderer.state.LightmapRenderState'
//~ if >=1.19 'net.minecraft.world.level.Level' -> 'net.minecraft.world.level.dimension.DimensionType'
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(LightTexture.class)
public class FullBrightMixin {

    //? if >=1.21.11 {
    //~ if >=26.0 '"updateLightTexture"' -> '"render"'
    //~ if >=26.0 '"Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"' -> '"Lnet/minecraft/client/renderer/state/LightmapRenderState;brightness:F"'
    @WrapOperation(method = "updateLightTexture", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"))
    //~ if >=26.0 DimensionType -> LightmapRenderState
    private float getBrightness(DimensionType instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled()) {
            return 1.0f;
        }
        return original.call(instance);
    }
    //? } else {
    /*@Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private /^? if > 1.18.2 >>^/static void onGetBrightness(
            //? if > 1.18.2 {
            DimensionType dimensionType,
            //? } else
            //Level level,
            int lightLevel,
            CallbackInfoReturnable<Float> cir
    ) {
        if (Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled()) {
            cir.setReturnValue(1.0f);
        }
    }
    *///? }
}
//~ }
