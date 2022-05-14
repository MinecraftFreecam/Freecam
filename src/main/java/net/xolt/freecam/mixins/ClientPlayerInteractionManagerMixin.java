package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

//    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
//    private void onInteractBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
//        if (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract) {
//            cir.setReturnValue(ActionResult.FAIL);
//        }
//    }
//
//    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
//    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//        if (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract) {
//            cir.setReturnValue(false);
//        }
//    }
//
//    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
//    private void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
//        if (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract) {
//            cir.setReturnValue(false);
//        }
//    }
//
//    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
//    private void onUpdateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
//        if (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract) {
//            cir.setReturnValue(false);
//        }
//    }
//
//    @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
//    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//        if (entity.equals(MC.player) || (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract)) {
//            cir.setReturnValue(ActionResult.FAIL);
//        }
//    }
//
//    @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
//    private void onInteractEntityAtLocation(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//        if (entity.equals(MC.player) || (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract)) {
//            cir.setReturnValue(ActionResult.FAIL);
//        }
//    }
//
//    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
//    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
//        if (target.equals(MC.player) || (Freecam.isEnabled() && !ModConfig.INSTANCE.allowInteract)) {
//            ci.cancel();
//        }
//    }
}
