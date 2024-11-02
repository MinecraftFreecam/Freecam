package net.xolt.freecam.fabric.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.xolt.freecam.Freecam.MC;
import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILHARD;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Final private RenderBuffers renderBuffers;

    @Shadow protected abstract void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource);

    // Makes the player render if showPlayer is enabled.
    @Inject(method = "renderEntities", at = @At("TAIL"), locals = CAPTURE_FAILHARD)
    private void onRender(
            PoseStack poseStack,
                          MultiBufferSource.BufferSource bufferSource,
                          Camera camera,
                          DeltaTracker deltaTracker,
                          List<Entity> entities,
                          CallbackInfo ci) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.showPlayer) {
            Vec3 position = camera.getPosition();
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            renderEntity(MC.player, position.x, position.y, position.z, partialTick, poseStack, renderBuffers.bufferSource());
        }
    }
}
