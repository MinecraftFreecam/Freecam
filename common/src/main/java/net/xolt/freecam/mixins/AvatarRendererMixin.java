package net.xolt.freecam.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
//? } else {
/*import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
*///? }

//? if <1.21.11
//import static net.xolt.freecam.Freecam.MC;

//? if >= 1.21.11 {
@Mixin(AvatarRenderer.class)
//? } else
//@Mixin(EntityRenderer.class)
public class AvatarRendererMixin {

    //? if >= 1.21.11 {
    // Prevent rendering of nametag in inventory screen
    @Inject(method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void onSubmitNameTag(AvatarRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (Freecam.isEnabled() && renderState.shadowPieces.isEmpty()) {
            ci.cancel();
        }
    }
    //? } else {
    /*@Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(Entity renderState,
                               Component component,
                               PoseStack poseStack,
                               MultiBufferSource multiBufferSource,
                               int packedLightCoords,
                               //? if >=1.20.6
                               float partialTick,
                               CallbackInfo ci) {
        if (Freecam.isEnabled() && !MC.getEntityRenderDispatcher().shouldRenderShadow) {
            ci.cancel();
        }
    }
    *///? }
}
