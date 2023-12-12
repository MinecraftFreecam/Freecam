package net.xolt.freecam.mixins;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Disables block outlines when allowInteract is disabled.
    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && (!ModConfig.INSTANCE.utility.allowInteract || (!Freecam.canUseCheats() && !ModConfig.INSTANCE.utility.interactionMode.equals(ModConfig.InteractionMode.PLAYER)))) {
            cir.setReturnValue(false);
        }
    }

    // Makes mouse clicks come from the player rather than the freecam entity when player control is enabled or if interaction mode is set to player.
    @ModifyVariable(method = "updateTargetedEntity", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"))
    private Entity onUpdateTargetedEntity(Entity entity) {
        if (Freecam.isEnabled() && (Freecam.isPlayerControlEnabled() || ModConfig.INSTANCE.utility.interactionMode.equals(ModConfig.InteractionMode.PLAYER))) {
            return MC.player;
        }
        return entity;
    }
}
