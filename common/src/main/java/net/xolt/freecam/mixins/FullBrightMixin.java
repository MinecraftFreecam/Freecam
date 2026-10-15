package net.xolt.freecam.mixins;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//~ if >=26.1 LightTexture -> Lightmap
import net.minecraft.client.renderer.Lightmap;

//? if <26.1 >=1.19 {
/*import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
*///? } else {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import static org.objectweb.asm.Opcodes.GETFIELD;
//? }

//~ if >=26.1 LightTexture -> Lightmap
@Mixin(Lightmap.class)
public class FullBrightMixin {

    @Unique
    private boolean freecam$useFullBright() {
        return Freecam.isEnabled() && ModConfig.get().isFullBrightEnabled();
    }

    //? if >=26.1 {
    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = GETFIELD, target = "Lnet/minecraft/client/renderer/state/LightmapRenderState;brightness:F"))
    private float onGetBrightness(float original) {
        return freecam$useFullBright() ? 16.0f : original;
    }
    //? } else if >=1.19 {
    /*@Expression("this.minecraft.options.gamma().get()")
    @Definition(id = "minecraft", field = "Lnet/minecraft/client/renderer/LightTexture;minecraft:Lnet/minecraft/client/Minecraft;")
    @Definition(id = "options", field = "Lnet/minecraft/client/Minecraft;options:Lnet/minecraft/client/Options;")
    @Definition(id = "gamma", method = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;")
    @Definition(id = "get", method = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;")
    @WrapOperation(method = "updateLightTexture", at = @At("MIXINEXTRAS:EXPRESSION"))
    @SuppressWarnings("unchecked")
    private <T> T onGetGamma(net.minecraft.client.OptionInstance<T> instance, Operation<T> original) {
        return freecam$useFullBright() ? (T) (Double) 16.0 : original.call(instance);
    }
    *///? } else {
    /*@ModifyExpressionValue(method = "updateLightTexture", at = @At(value = "FIELD", opcode = GETFIELD, target = "Lnet/minecraft/client/Options;gamma:D"))
    private double onGetGamma(double original) {
        return freecam$useFullBright() ? 16.0 : original;
    }
    *///? }
}
