package net.xolt.freecam.fabric.mixins;


import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.11 {
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
//? } else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
*///? }
//? if <1.21.11 && >=1.20.6 {
/*import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4fStack;
*///? }
//? if >=1.20.6 {
import net.minecraft.world.TickRateManager;
import net.minecraft.client.renderer.culling.Frustum;
//? }
//? if >1.18.2 && <1.21.11 {
/*import org.joml.Matrix4f;
*///? }
//? if <= 1.18.2 {
/*import com.mojang.math.Matrix4f;
*///? }

import static net.xolt.freecam.Freecam.MC;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    // Makes the player render if showPlayer is enabled.
    //? if >=1.21.11 {
    @Shadow protected abstract EntityRenderState extractEntity(Entity e, float g);

    @Inject(method = "extractVisibleEntities", at = @At(value = "RETURN"))
    private void onExtractVisibleEntities(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState renderState, CallbackInfo ci) {
        if (MC.level != null && Freecam.isEnabled() && ModConfig.INSTANCE.visual.showPlayer) {
            Entity player = MC.player;
            TickRateManager tickRateManager = MC.level.tickRateManager();
            float g = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(player));
            EntityRenderState entityRenderState = this.extractEntity(player, g);
            renderState.entityRenderStates.add(entityRenderState);
        }
    }
    //? } else {
    /*@Shadow @Final
    private RenderBuffers renderBuffers;

    @Shadow protected abstract void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    //? if >=1.20.6 {
    private void onRender(float partialTick,
                          long nanoTime,
                          boolean renderBlockOutline,
                          Camera camera,
                          GameRenderer gameRenderer,
                          LightTexture lightTexture,
                          Matrix4f matrix4f,
                          Matrix4f matrix4f2,
                          CallbackInfo ci,
                          // Local capture needed for poseStack
                          TickRateManager tickRateManager,
                          float g,
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
        onRender(new Vec3(x, y, z), partialTick, poseStack, renderBuffers);
    }
    //? } else {
    /^private void onRender(PoseStack matrices,
                          float tickDelta,
                          long limitTime,
                          boolean renderBlockOutline,
                          Camera camera,
                          GameRenderer gameRenderer,
                          LightTexture lightmapTextureManager,
                          Matrix4f positionMatrix,
                          CallbackInfo ci) {
        onRender(camera.getPosition(), tickDelta, matrices, renderBuffers);
    }
    ^///? }

    @Unique
    private void onRender(Vec3 cameraPos, float partialTick, PoseStack poseStack, RenderBuffers renderBuffers) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.visual.showPlayer) {
            renderEntity(MC.player, cameraPos.x, cameraPos.y, cameraPos.z, partialTick, poseStack, renderBuffers.bufferSource());
        }
    }
    *///? }
}
