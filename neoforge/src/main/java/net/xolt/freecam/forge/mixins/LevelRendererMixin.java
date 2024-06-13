package net.xolt.freecam.forge.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    // Disable player rendering if show player is disabled
    // (non-camera LocalPlayers are rendered by default on Forge)
    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderEntity(Entity entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        if (entity == MC.player && Freecam.isEnabled() && !ModConfig.INSTANCE.visual.showPlayer) {
            ci.cancel();
        }
    }
}
