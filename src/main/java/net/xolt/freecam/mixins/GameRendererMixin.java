package net.xolt.freecam.mixins;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getLight(Lnet/minecraft/entity/Entity;F)I"))
    private Entity onRenderHand(Entity entity) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showHand) {
            return Freecam.getFreeCamera();
        }
        return entity;
    }
}
