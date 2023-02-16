package net.xolt.freecam.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow Entity focusedEntity;
    @Shadow float lastCameraY;
    @Shadow float cameraY;

    // When toggling freecam, update the camera's eye height instantly without any transition.
    @Inject(method = "update", at = @At("HEAD"))
    public void onUpdate(BlockView area, Entity newFocusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (newFocusedEntity == null || this.focusedEntity == null || newFocusedEntity.equals(this.focusedEntity)) {
            return;
        }

        if (newFocusedEntity instanceof FreeCamera || this.focusedEntity instanceof FreeCamera) {
            this.lastCameraY = this.cameraY = newFocusedEntity.getStandingEyeHeight();
        }
    }
}
