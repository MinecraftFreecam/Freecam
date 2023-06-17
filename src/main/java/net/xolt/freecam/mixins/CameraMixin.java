package net.xolt.freecam.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.BlockView;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow
    private Entity focusedEntity;

    @Shadow
    private float lastCameraY;

    @Shadow
    private float cameraY;

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

    // Removes the submersion overlay when underwater, in lava, or powdered snow.
    @Inject(method = "getSubmergedFluidState", at = @At("HEAD"), cancellable = true)
    public void onGetSubmersionType(CallbackInfoReturnable<FluidState> cir) {
        if (Freecam.isEnabled() && !ModConfig.INSTANCE.visual.showSubmersion) {
            cir.setReturnValue(Fluids.EMPTY.getDefaultState());
        }
    }
}
