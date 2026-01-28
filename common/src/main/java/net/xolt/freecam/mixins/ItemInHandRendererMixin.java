package net.xolt.freecam.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.xolt.freecam.Freecam;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//? } else {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///? }

import static net.xolt.freecam.Freecam.MC;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Unique private float freecam$tickDelta;

    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getViewXRot(F)F"
            )
    )
    private float redirectGetViewXRot(LocalPlayer player, float partialTick) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().getViewXRot(partialTick) : player.getViewXRot(partialTick);
    }

    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getViewYRot(F)F"
            )
    )
    private float redirectGetViewYRot(LocalPlayer player, float partialTick) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().getViewYRot(partialTick) : player.getViewYRot(partialTick);
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;xBob:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetXBob(LocalPlayer player) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().xBob : player.xBob;
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;xBobO:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetXBobO(LocalPlayer player) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().xBobO : player.xBobO;
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;yBob:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetYBob(LocalPlayer player) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().yBob : player.yBob;
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;yBobO:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetYBobO(LocalPlayer player) {
        return Freecam.isEnabled() ? Freecam.getFreeCamera().yBobO : player.yBobO;
    }

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
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
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private int onRenderItemSetLight(int light) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(Freecam.getFreeCamera(), freecam$tickDelta);
        }
        return light;
    }
}
