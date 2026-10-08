package net.xolt.freecam.mixins;

import net.minecraft.client.player.LocalPlayer;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >=26.3 {
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.util.Mth;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? } else {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.ItemInHandRenderer;
import static org.objectweb.asm.Opcodes.GETFIELD;
*///? }

/// Make first-person hand & held item view bobbing & rotation use the camera,
/// instead of the player.

//~ if >=26.3 ItemInHandRenderer -> FirstPersonHandsAndItems
@Mixin(FirstPersonHandsAndItems.class)
public class FirstPersonHandRotationMixin {

    //? if >=26.3 {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(LocalPlayer player, float partialTicks, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            FreeCamera camera = Freecam.getFreeCamera();
            state.viewXRot = camera.getViewXRot(partialTicks);
            state.viewYRot = camera.getViewYRot(partialTicks);
            state.xBob = Mth.lerp(partialTicks, camera.xBobO(), camera.xBob());
            state.yBob = Mth.lerp(partialTicks, camera.yBobO(), camera.yBob());
        }
    }

    //? } else {
    /*//~ if >=26.2 renderHandsWithItems -> submitHandsWithItems {

    private float onSubmitHandsWithItems_player_getViewXRot(LocalPlayer player, float partialTick, Operation<Float> original) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().getViewXRot(partialTick) : original.call(player, partialTick);
    }

    @WrapOperation(method = "submitHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getViewYRot(F)F"))
    private float onSubmitHandsWithItems_player_getViewYRot(LocalPlayer player, float partialTick, Operation<Float> original) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().getViewYRot(partialTick) : original.call(player, partialTick);
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyExpressionValue(method = "submitHandsWithItems", at = @At(value = "FIELD", opcode = GETFIELD, target = "Lnet/minecraft/client/player/LocalPlayer;xBob:F"))
    private float onSubmitHandsWithItems_player_xBob(float original) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().xBob() : original;
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyExpressionValue(method = "submitHandsWithItems", at = @At(value = "FIELD", opcode = GETFIELD, target = "Lnet/minecraft/client/player/LocalPlayer;xBobO:F"))
    private float onSubmitHandsWithItems_player_xBobO(float original) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().xBobO() : original;
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyExpressionValue(method = "submitHandsWithItems", at = @At(value = "FIELD", opcode = GETFIELD, target = "Lnet/minecraft/client/player/LocalPlayer;yBob:F"))
    private float onSubmitHandsWithItems_player_yBob(float original) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().yBob() : original;
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyExpressionValue(method = "submitHandsWithItems", at = @At(value = "FIELD", opcode = GETFIELD, target = "Lnet/minecraft/client/player/LocalPlayer;yBobO:F"))
    private float onSubmitHandsWithItems_player_yBobO(float original) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().yBobO() : original;
    }

    //~ }
    *///? }
}
