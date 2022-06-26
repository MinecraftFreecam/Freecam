package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    // Fixes night vision not working underwater
    @ModifyVariable(method = "render", at = @At(value = "STORE"))
    private static ClientPlayerEntity onRender(ClientPlayerEntity entity) {
        if (Freecam.isEnabled()) {
            return (ClientPlayerEntity)MC.getCameraEntity();
        }
        return entity;
    }

    // Fixes night vision not working underwater
    @ModifyVariable(method = "applyFog", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;"))
    private static Entity onApplyFog(Entity entity) {
        if (Freecam.isEnabled()) {
            return MC.getCameraEntity();
        }
        return entity;
    }
}
