package net.xolt.freecam.mixins;

//~ if >=26.0 LightTexture -> Lightmap
import net.minecraft.client.renderer.Lightmap;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? } else {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///? }
//? if >=26.1 {
import net.minecraft.client.renderer.state.LightmapRenderState;
//? } else if >=1.19 {
/*import net.minecraft.world.level.dimension.DimensionType;
*///? } else {
/*import net.minecraft.world.level.Level;
*///? }

//~ if >=26.0 LightTexture -> Lightmap
@Mixin(Lightmap.class)
public class FullBrightMixin {

    //? if >=26.1 {
    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/LightmapRenderState;brightness:F"))
    private float getBrightness(LightmapRenderState instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled()) {
            return 16.0f;
        }
        return original.call(instance);
    }
    //? } else if >=1.21.11 {
    /*@WrapOperation(method = "updateLightTexture", at = @At(value="INVOKE", target="Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"))
    private float getBrightness(DimensionType instance, Operation<Float> original) {
        if (Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled()) {
            return 1.0f;
        }
        return original.call(instance);
    }
    *///? } else {
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
