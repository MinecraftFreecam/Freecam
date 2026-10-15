package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.xolt.freecam.Freecam.MC;

//~ if >=26.1 'renderer.GameRenderer' -> Minecraft
import net.minecraft.client.Minecraft;

/// When [`player control`][Freecam#isPlayerControlEnabled()] is enabled or `interaction mode` is [`player`][ModConfig#allowInteractionsFromPlayer()],
/// ensure mouse clicks come from the player rather than the camera.

//~ if >=26.1 GameRenderer -> Minecraft
@Mixin(Minecraft.class)
public class HitTargetSourceMixin {
    @ModifyExpressionValue(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity onGetHitTargetSource(Entity original) {
        return Freecam.isEnabled() && (Freecam.isPlayerControlEnabled() || ModConfig.get().allowInteractionsFromPlayer())
            ? MC.player
            : original;
    }
}
