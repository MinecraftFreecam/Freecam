package net.xolt.freecam.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(Camera.class)
public class CameraMixin {

    // Makes the player render if showPlayer is enabled.
    @Inject(method = "getFocusedEntity", at = @At("HEAD"), cancellable = true)
    private void onGetFocusedEntity(CallbackInfoReturnable<Entity> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showPlayer) {
            cir.setReturnValue(MC.player);
        }
    }

    // Makes the game think third person is enabled so the focused entity is rendered.
    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
    private void onIsThirdPerson(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showPlayer) {
            cir.setReturnValue(true);
        }
    }

}
