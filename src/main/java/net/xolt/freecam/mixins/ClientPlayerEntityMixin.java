package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyArgs(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private void onTickMovement(Args args) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.flightMode.equals(ModConfig.FlightMode.CREATIVE) && this.equals(Freecam.getFreeCamera())) {
            args.set(1, ((Double) args.get(1) / (ModConfig.INSTANCE.horizontalSpeed / 10)) * (ModConfig.INSTANCE.verticalSpeed / 10));
        }
    }
}
