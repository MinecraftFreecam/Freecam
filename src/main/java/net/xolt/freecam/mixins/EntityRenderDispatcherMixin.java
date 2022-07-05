package net.xolt.freecam.mixins;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

  // Prevents shadow being cast when Iris is enabled.
  @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
  private void onShouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
    if (Freecam.isEnabled() && entity.equals(Freecam.getFreeCamera())) {
      cir.setReturnValue(false);
    }
  }
}
