//? if >=1.21.11 {
package net.xolt.freecam.mixins;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.2 {
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.gen.Invoker;
//? } else {
/*import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
*///? }

import static net.xolt.freecam.Freecam.MC;

/// When Freecam is active and the ['outlines' feature][Freecam#isOutlineEnabled()]
/// is enabled, ensure the local player is visible and has
/// ['show entity outlines'][LevelRenderState#shouldShowEntityOutlines] enabled.

//~ if >=26.2 LevelRenderer -> LevelExtractor
@Mixin(LevelExtractor.class)
public abstract class EnablePlayerOutlineMixin {

    //? if >=26.2 {
    @Invoker("extractEntity")
    abstract EntityRenderState freecam$extractEntity(final Entity entity, final float partialTickTime);
    //? } else {
    /*@Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Unique
    private EntityRenderState freecam$extractEntity(final Entity entity, final float partialTickTime) {
        return entityRenderDispatcher.extractEntity(entity, partialTickTime);
    }
    *///? }

    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    private void onExtractVisibleEntities(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo ci) {
        if (Freecam.isEnabled() && Freecam.isOutlineEnabled()) {
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            EntityRenderState state = freecam$extractEntity(MC.player, partialTick);
            state.outlineColor = ARGB.opaque(MC.player.getTeamColor());
            levelRenderState.entityRenderStates.add(state);
            //~ if >=26.2 haveGlowingEntities -> shouldShowEntityOutlines
            levelRenderState.shouldShowEntityOutlines = true;
        }
    }
}
//? }
