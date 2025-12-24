package net.xolt.freecam.mixins;

import net.minecraft.client.Camera;
import net.xolt.freecam.config.ModConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.ARGB;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    // Add the local player to the visible entities when freecam is enabled so they get the outline
    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    private void onExtractVisibleEntities(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo ci) {
        if (Freecam.isEnabled() && MC.player != null) {
            
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            EntityRenderState state = entityRenderDispatcher.extractEntity(MC.player, partialTick);

            if (state != null && ModConfig.INSTANCE.visual.outlinePlayer) {
                state.outlineColor = ARGB.opaque(MC.player.getTeamColor());
                levelRenderState.entityRenderStates.add(state);
                levelRenderState.haveGlowingEntities = true;
            }
        }
    }
}
