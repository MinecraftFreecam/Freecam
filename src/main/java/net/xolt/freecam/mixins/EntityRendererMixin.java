package net.xolt.freecam.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    // Makes arm shading depend upon FreeCamera position rather than player position.
    @Inject(method = "getLight", at = @At("HEAD"), cancellable = true)
    private void onGetLight(Entity entity, float tickDelta, CallbackInfoReturnable<Integer> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showHand && entity.equals(MC.player)) {
            FreeCamera freeCamera = Freecam.getFreeCamera();
            EntityRenderer<? super FreeCamera> entityRenderer = MC.getEntityRenderDispatcher().getRenderer(freeCamera);
            cir.setReturnValue(entityRenderer.getLight(freeCamera, tickDelta));
        }
    }
}
