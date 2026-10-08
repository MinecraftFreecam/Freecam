package net.xolt.freecam.mixins;

import net.minecraft.world.entity.player.Player;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.2 {
import net.minecraft.client.gui.Hud;
//? } else {
/*import net.minecraft.client.gui.Gui;
*///? }

import static net.xolt.freecam.Freecam.MC;

/// Makes HUD correspond to the player, rather than the camera.

//~ if >=26.2 Gui -> Hud
@Mixin(Hud.class)
public class HudPlayerMixin {

    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(MC.player);
        }
    }
}
