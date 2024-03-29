package net.xolt.freecam.mixins;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BubbleColumnAmbientSoundHandler.class)
public class BubbleColumnAmbientSoundHandlerMixin {

    @Shadow @Final private LocalPlayer player;

    // Prevent bubble column sound in freecam
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (player instanceof FreeCamera) {
            ci.cancel();
        }
    }
}
