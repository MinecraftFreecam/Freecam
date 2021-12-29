package net.xolt.freecam.mixins;

import net.minecraft.client.option.GameOptions;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Inject(method = "setPerspective", at = @At("HEAD"), cancellable = true)
    private void onSetPerspective(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
