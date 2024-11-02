package net.xolt.freecam.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    // Prevent rendering of nametag in inventory screen
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(EntityRenderState entityRenderState, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (Freecam.isEnabled() && !MC.getEntityRenderDispatcher().shouldRenderShadow) {
            ci.cancel();
        }
    }
}
