package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    // Prevents interacting with blocks when allowInteract is disabled.
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity.equals(MC.player) || (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
    private void onInteractEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity.equals(MC.player) || (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    // Prevents attacking self.
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target.equals(MC.player)) {
            ci.cancel();
        }
    }
}
