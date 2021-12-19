package net.xolt.freecam.mixins;

import net.minecraft.entity.player.PlayerInventory;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(method = "updateItems", at = @At("TAIL"))
    private void onUpdateItems(CallbackInfo ci) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showClone && Freecam.getClone() != null) {
            Freecam.getClone().updateInventory();
        }
    }
}
