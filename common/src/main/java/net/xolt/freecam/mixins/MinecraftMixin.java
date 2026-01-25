package net.xolt.freecam.mixins;

import net.minecraft.client.Minecraft;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.config.ModBindings.KEY_TOGGLE;
import static net.xolt.freecam.config.ModBindings.KEY_TRIPOD_RESET;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // Prevents attacks when allowInteract is disabled.
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (freecam$disableInteract()) {
            cir.cancel();
        }
    }

    // Prevents item pick when allowInteract is disabled.
    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        if (freecam$disableInteract()) {
            ci.cancel();
        }
    }

    // Prevents block breaking when allowInteract is disabled.
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(CallbackInfo ci) {
        if (freecam$disableInteract()) {
            ci.cancel();
        }
    }

    // Prevents hotbar keys from changing selected slot when freecam key is held
    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 2), cancellable = true)
    private void onHandleInputEvents(CallbackInfo ci) {
        if (KEY_TOGGLE.get().isDown() || KEY_TRIPOD_RESET.get().isDown()) {
            ci.cancel();
        }
    }

    // Disables freecam if the player disconnects.
    @Inject(
            //? if >=1.21.11 {
            method = "disconnect",
            //? } else if >=1.20.6 {
            /*method = "disconnect()V",
            *///? } else
            //method = "clearLevel()V",
            at = @At(value = "HEAD")
    )
    private void onDisconnect(CallbackInfo ci) {
        Freecam.onDisconnect();
    }

    @Unique
    private static boolean freecam$disableInteract() {
        return Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !ModConfig.INSTANCE.utility.allowInteract;
    }
}
