package net.xolt.freecam.mixins;

//? if >=26.2 {

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {

    @Invoker
    abstract EntityRenderState callExtractEntity(final Entity entity, final float partialTickTime);

    // Add the local player to the visible entities when freecam is enabled so they get the outline
    // NOTE: was in LevelRendererMixin until 26.2
    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    private void onExtractVisibleEntities(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo ci) {
        if (Freecam.isEnabled() && Freecam.isOutlineEnabled()) {
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            EntityRenderState state = callExtractEntity(MC.player, partialTick);
            state.outlineColor = ARGB.opaque(MC.player.getTeamColor());
            levelRenderState.entityRenderStates.add(state);
            levelRenderState.shouldShowEntityOutlines = true;
        }
    }
}
//? }
