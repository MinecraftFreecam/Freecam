//? if >=26.3 {
package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.xolt.freecam.Freecam.MC;

/// Moved from [ItemInHandRendererMixin] in 26.3
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {

    // Makes arm shading depend upon FreeCamera position rather than player position.
    @ModifyVariable(
        method = "submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
        at = @At("HEAD"),
        ordinal = 0, // First int parameter
        argsOnly = true)
    private int onSubmitArmWithItem_lightCoordsArg(int lightCoords, @Local(argsOnly = true, ordinal = 0) float partialTick) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(Freecam.getFreeCamera(), partialTick);
        }
        return lightCoords;
    }
}
//? }
