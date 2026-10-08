package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.player.Player;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @ModifyReturnValue(method = "getCameraPlayer", at = @At("RETURN"))
    private Player onGetCameraPlayer(Player original) {
        return Freecam.isEnabled() ? MC.player : original;
    }
}
