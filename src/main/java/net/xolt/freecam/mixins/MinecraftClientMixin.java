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
            Motion.doMotion(ModConfig.INSTANCE.freecamHSpeed, ModConfig.INSTANCE.freecamVSpeed);
            MC.player.setOnGround(true);
        }
    }
}
