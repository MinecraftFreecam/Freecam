package net.xolt.freecam.mixins;

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
    @ModifyVariable(method = "render", at = @At("STORE"))
    private static Entity onRender(Entity entity) {
        if (Freecam.isEnabled()) {
            return MC.getCameraEntity();
        }
        return entity;
    }

    // Fixes night vision not working underwater
    @ModifyVariable(method = "applyFog", at = @At("STORE"))
    private static Entity onApplyFog(Entity entity) {
        if (Freecam.isEnabled()) {
            return MC.getCameraEntity();
        }
        return entity;
    }
}
