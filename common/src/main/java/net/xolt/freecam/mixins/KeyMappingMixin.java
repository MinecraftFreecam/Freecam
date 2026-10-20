package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.keys.KeyStateHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

/// Capture and track effective input state before it is [transformed][net.minecraft.client.ToggleKeyMapping#setDown(boolean)]
/// into logical toggle state. This is important to prevent 'toggle sneak' from affecting camera movement in Freecam.
///
/// Also prevent the logical 'toggle sneak' state from updating while Freecam has input control.
/// Otherwise, the player can change sneak state when Freecam restores control back to the player.
///
/// @see KeyStateHolder
@Mixin(KeyMapping.class)
abstract class KeyMappingMixin {

    // Capture key release separately from logical toggle state.
    @Inject(method = "release", at = @At("HEAD"))
    private void onRelease(CallbackInfo ci) {
        KeyStateHolder.update((KeyMapping) (Object) this, false);
    }

    /// Capture effective input state during in-game focus update sync.
    @WrapOperation(method = "setAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V"))
    private static void onSetAll_setDown(KeyMapping instance, boolean state, Operation<Void> original) {
        KeyStateHolder.update(instance, state);
        original.call(instance, state);
    }

    /// Capture effective input state at the boundary where input events update [KeyMapping] state.
    @WrapMethod(method = "set")
    private static void onSet(InputConstants.Key key, boolean state, Operation<Void> original) {
        KeyStateHolder.update(key, state);
        original.call(key, state);
    }

    /// Avoid updating 'toggle sneak' state while Freecam has control.
    @SuppressWarnings("ConstantValue")
    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void onSet(boolean down, CallbackInfo ci) {
        if ((Object) this == MC.options.keyShift && Freecam.isEnabled() && !Freecam.isPlayerControlEnabled()) {
            ci.cancel();
        }
    }
}
