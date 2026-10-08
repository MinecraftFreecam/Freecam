package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//~ if >=26.3 ItemInHandRenderer -> FirstPersonHandsAndItemsRenderer
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;

import static net.xolt.freecam.Freecam.MC;

/// Makes first-person hand & held-item shading use FreeCamera position,
/// rather than player position.

//~ if >=26.3 ItemInHandRenderer -> FirstPersonHandsAndItemsRenderer
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandShadingMixin {

    @ModifyVariable(
        //? if >=26.3 {
        method = "submitArmWithItem",
        //? } else if >=26.2 {
        /*method = "submitHandsWithItems",
        *///? } else {
        /*method = "renderHandsWithItems",
        *///? }
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0)
    private int onSubmitArmWithItem_lightCoordsArg(int lightCoords, @Local(argsOnly = true, ordinal = 0) float partialTick) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(Freecam.getFreeCamera(), partialTick);
        }
        return lightCoords;
    }
}
