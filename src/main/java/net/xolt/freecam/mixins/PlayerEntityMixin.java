package net.xolt.freecam.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "isBlockBreakingRestricted", at = @At("HEAD"), cancellable = true)
    public void onIsBlockBreakingRestricted(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }
}
