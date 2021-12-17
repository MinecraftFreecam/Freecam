package net.xolt.freecam.mixins;

import net.minecraft.client.MinecraftClient;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.Motion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            if (ModConfig.INSTANCE.freecamMode.equals(ModConfig.FlightMode.MODDED)) {
                Motion.doMotion(ModConfig.INSTANCE.freecamSpeed, ModConfig.INSTANCE.freecamSpeed * 0.8);
                MC.player.setOnGround(true);
            } else {
                MC.player.getAbilities().flying = true;
                MC.player.setOnGround(false);
            }
            if (ModConfig.INSTANCE.showClone && Freecam.getClone() != null) {
                Freecam.getClone().updateInventory();
            }
        }
    }
}
