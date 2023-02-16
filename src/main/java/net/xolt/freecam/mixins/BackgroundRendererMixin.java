package net.xolt.freecam.mixins;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.CameraSubmersionType;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    // Removes the submersion overlay when underwater, in lava, or powdered snow.
    @ModifyVariable(method = "applyFog", at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;",
            ordinal = 0))
    private static CameraSubmersionType onGetSubmersionType(CameraSubmersionType type) {
        if (Freecam.isEnabled() && !ModConfig.INSTANCE.showSubmersion) {
            return CameraSubmersionType.NONE;
        }
        return type;
    }
}
