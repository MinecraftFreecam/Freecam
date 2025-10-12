package net.xolt.freecam.mixins;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.variant.api.BuildVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;
import static net.xolt.freecam.config.ModConfig.InteractionMode.PLAYER;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Disables block outlines when allowInteract is disabled.
    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !freecam$allowInteract()) {
            cir.setReturnValue(false);
        }
    }

    // Makes mouse clicks come from the player rather than the freecam entity when player control is enabled or if interaction mode is set to player.
    @ModifyVariable(method = "pick(F)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity onUpdateTargetedEntity(Entity entity) {
        if (Freecam.isEnabled() && (Freecam.isPlayerControlEnabled() || ModConfig.INSTANCE.utility.interactionMode.equals(ModConfig.InteractionMode.PLAYER))) {
            return MC.player;
        }
        return entity;
    }

    @Unique
    private static boolean freecam$allowInteract() {
        return ModConfig.INSTANCE.utility.allowInteract && (BuildVariant.getInstance().cheatsPermitted() || ModConfig.INSTANCE.utility.interactionMode.equals(PLAYER));
    }
}
