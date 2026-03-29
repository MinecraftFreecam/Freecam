package net.xolt.freecam.mixins;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26.1 {
/*import org.spongepowered.asm.mixin.injection.Redirect;
*///? } else {
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? }
//? if <1.21.11
//import net.minecraft.world.level.BlockGetter;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private Entity entity;
    @Shadow private float eyeHeightOld;
    @Shadow private float eyeHeight;

    //? if >= 26.1
    //@Shadow protected abstract void alignWithEntity(float par1);

    // When toggling freecam, update the camera's eye height instantly without any transition.
    //? if >=26.1 {
    /*@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;alignWithEntity(F)V"))
    private void onAlignWithEntity(Camera instance, float tickDelta) {
        // FIXME: this approach is likely flawed.
        // We dont cancel the transition when _exiting_ freecam
        if (entity instanceof FreeCamera) {
            eyeHeightOld = eyeHeight = entity.getEyeHeight();
        } else {
            alignWithEntity(tickDelta);
        }
    }
    *///? } else {
    @Inject(method = "setup", at = @At("HEAD"))
    //~ if >=1.21.11 'BlockGetter area' -> 'Level level'
    public void onUpdate(Level level, Entity newFocusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (newFocusedEntity == null || this.entity == null || newFocusedEntity.equals(this.entity)) {
            return;
        }

        if (newFocusedEntity instanceof FreeCamera || this.entity instanceof FreeCamera) {
            this.eyeHeightOld = this.eyeHeight = newFocusedEntity.getEyeHeight();
        }
    }
    //? }

    // Removes the submersion overlay when underwater, in lava, or powdered snow.
    @Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
    public void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
        if (Freecam.isEnabled() && ModConfig.get().shouldHideSubmersionFog()) {
            cir.setReturnValue(FogType.NONE);
        }
    }
}
