package net.xolt.freecam.forge.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
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

    // Disable player rendering if show player is disabled (non-camera LocalPlayers are rendered by default on Forge)
    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        if (entity == MC.player && Freecam.isEnabled() && !ModConfig.INSTANCE.visual.showPlayer) {
            ci.cancel();
        }
    }
}
