//? if <26.3 {
/*package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//? } else {
/^import net.minecraft.client.renderer.MultiBufferSource;
^///? }

import static net.xolt.freecam.Freecam.MC;
import static org.objectweb.asm.Opcodes.GETFIELD;

/// Moved to [FirstPersonHandsAndItemsMixin] and [FirstPersonHandsAndItemsRenderer] in 26.3
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Unique private float freecam$tickDelta;

    @WrapOperation(method = "submitHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getViewXRot(F)F"))
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

    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void storeTickDelta(float partialTick, PoseStack poseStack,
                                //? if >=1.21.11 {
                                SubmitNodeCollector nodeCollector,
                                //? } else
                                //MultiBufferSource.BufferSource vertexConsumers,
                                LocalPlayer player,
                                int packedLight,
                                CallbackInfo ci) {
        this.freecam$tickDelta = partialTick;
    }

    // Makes arm shading depend upon FreeCamera position rather than player position.
    @ModifyVariable(method = "submitHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private int onRenderItemSetLight(int lightCoords) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(Freecam.getFreeCamera(), freecam$tickDelta);
        }
        return lightCoords;
    }
}
*///? }
