package net.xolt.freecam.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    // Prevents player from being controlled when freecam is enabled.
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            if (MC.player != null && MC.player.input instanceof KeyboardInput && !Freecam.isPlayerControlEnabled()) {
                Input input = new Input();
                input.sneaking = MC.player.input.sneaking; // Makes player continue to sneak after freecam is enabled.
                MC.player.input = input;
            }
            ((GameRendererAccessor) MC.gameRenderer).setRenderHand(ModConfig.INSTANCE.showHand);
        }
    }

    // Prevents attacks when allowInteract is disabled.
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfo ci) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !ModConfig.INSTANCE.allowInteract) {
            ci.cancel();
        }
    }

    // Prevents item pick when allowInteract is disabled.
    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !ModConfig.INSTANCE.allowInteract) {
            ci.cancel();
        }
    }

    // Prevents block breaking when allowInteract is disabled.
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(CallbackInfo ci) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !ModConfig.INSTANCE.allowInteract) {
            ci.cancel();
        }
    }

    // Prevents hotbar keys from changing selected slot when freecam key is held
    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 2), cancellable = true)
    private void onHandleInputEvents(CallbackInfo ci) {
        if (Freecam.getFreecamBind().isPressed() || Freecam.getTripodResetBind().isPressed()) {
            ci.cancel();
        }
    }
}
