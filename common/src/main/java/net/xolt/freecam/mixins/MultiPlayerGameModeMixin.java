package net.xolt.freecam.mixins;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.variant.api.BuildVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;
import static net.xolt.freecam.config.ModConfig.InteractionMode.PLAYER;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    // Prevents interacting with blocks when allowInteract is disabled.
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(LocalPlayer player, ClientLevel level, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (freecam$disableInteract()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteractEntity(Player player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (entity.equals(MC.player) || freecam$disableInteract()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void onInteractEntityAtLocation(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (entity.equals(MC.player) || freecam$disableInteract()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // Prevents attacking self.
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        if (target.equals(MC.player)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean freecam$disableInteract() {
        return Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !freecam$allowInteract();
    }

    @Unique
    private static boolean freecam$allowInteract() {
        return ModConfig.INSTANCE.utility.allowInteract && (BuildVariant.getInstance().cheatsPermitted() || ModConfig.INSTANCE.utility.interactionMode.equals(PLAYER));
    }
}
