package net.xolt.freecam.mixins;

import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(Entity.class)
@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class EntityMixin {

    // Makes mouse input rotate the FreeCamera.
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double rotation, double pitch, CallbackInfo ci) {
        if (Freecam.isEnabled() && this.equals(MC.player) && !Freecam.isPlayerControlEnabled()) {
            Freecam.getFreeCamera().turn(rotation, pitch);
            ci.cancel();
        }
    }

    // Prevents FreeCamera from pushing/getting pushed by entities.
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void onPushAwayFrom(Entity entity, CallbackInfo ci) {
        if (Freecam.isEnabled() && (entity.equals(Freecam.getFreeCamera()) || this.equals(Freecam.getFreeCamera()))) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "setDeltaMovement(DDD)V", at = @At("HEAD"), cancellable = true)
    private void onSetVelocity(CallbackInfo ci) {
        if (freecam$shouldFreeze()) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    private void onUpdateVelocity(CallbackInfo ci) {
        if (freecam$shouldFreeze()) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "setPos(DDD)V", at = @At("HEAD"), cancellable = true)
    private void onSetPosition(CallbackInfo ci) {
        if (freecam$shouldFreeze()) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "setPosRaw", at = @At("HEAD"), cancellable = true)
    private void onSetPos(CallbackInfo ci) {
        if (freecam$shouldFreeze()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean freecam$shouldFreeze() {
        return Freecam.isEnabled() && this.equals(MC.player) && freecam$allowFreeze();
    }

    @Unique
    private boolean freecam$allowFreeze() {
        return ModConfig.INSTANCE.utility.freezePlayer && !Freecam.isPlayerControlEnabled();
    }
}
