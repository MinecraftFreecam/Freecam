package net.xolt.freecam.mixins;

import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ChunkBorderDebugRenderer.class)
public class ChunkBorderDebugRendererMixin {
    
    @ModifyVariable(method = "render", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;"))
    private Entity onGetFocusedEntity(Entity entity) {
        if (Freecam.isEnabled()) {
            // When ShowPlayer is enabled, FreeCamera.getFocusedEntity returns MC.player.
            // When rendering chunk borders, we actually want the camera's position not the player's.
            return MC.getCameraEntity();
        }
        return entity;
    }

}
