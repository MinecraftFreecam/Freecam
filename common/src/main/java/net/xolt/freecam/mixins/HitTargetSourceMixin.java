package net.xolt.freecam.mixins;

import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.xolt.freecam.Freecam.MC;

//~ if >=26.1 'renderer.GameRenderer' -> Minecraft
import net.minecraft.client.Minecraft;

/// When [`player control`][Freecam#isPlayerControlEnabled()] is enabled or `interaction mode` is [`player`][ModConfig#allowInteractionsFromPlayer()],
/// ensure mouse clicks come from the player rather than the camera.

//~ if >=26.1 GameRenderer -> Minecraft
@Mixin(Minecraft.class)
public class HitTargetSourceMixin {
    @ModifyVariable(method = "pick(F)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity onGetHitTargetSource(Entity entity) {
        return Freecam.isEnabled() && (Freecam.isPlayerControlEnabled() || ModConfig.get().allowInteractionsFromPlayer())
            ? MC.player
            : entity;
    }
}
