package net.xolt.freecam.fabric.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;
import static org.spongepowered.asm.mixin.injection.callback.LocalCapture.CAPTURE_FAILHARD;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Final private RenderBuffers renderBuffers;

    @Shadow protected abstract void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource);

    // Makes the player render if showPlayer is enabled.
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0), locals = CAPTURE_FAILHARD)
    private void onRender(DeltaTracker deltaTracker,
                          boolean renderBlockOutline,
                          Camera camera,
                          GameRenderer gameRenderer,
                          LightTexture lightTexture,
                          Matrix4f matrix4f,
                          Matrix4f matrix4f2,
                          CallbackInfo ci,
                          // Local capture needed for poseStack
                          TickRateManager tickRateManager,
                          float partialTick,
                          ProfilerFiller profilerFiller,
                          Vec3 cameraPosition,
                          double x,
                          double y,
                          double z,
                          boolean frustumNotNull,
                          Frustum frustum,
                          float renderDistance,
                          boolean fog,
                          Matrix4fStack modelViewStack,
                          boolean bl4,
                          PoseStack poseStack,
                          MultiBufferSource.BufferSource bufferSource) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.showPlayer) {
            renderEntity(MC.player, x, y, z, partialTick, poseStack, renderBuffers.bufferSource());
        }
    }
}
