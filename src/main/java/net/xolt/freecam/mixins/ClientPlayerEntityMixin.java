package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(CallbackInfoReturnable cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showClone && Freecam.getClone() != null) {
            Freecam.getClone().animateDamage();
        }
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            MC.player.noClip = true;
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    private void onStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled()) {
            Freecam.updateRiding(entity, force);
            cir.cancel();
        }
    }
}
