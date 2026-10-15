package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.accessors.KeyMappingInternalAccessor;
import net.xolt.freecam.accessors.KeyMappingStateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static net.xolt.freecam.Freecam.MC;

/// Capture and track effective input state before it is [transformed][net.minecraft.client.ToggleKeyMapping#setDown(boolean)]
/// into logical toggle state. This is important to prevent 'toggle sneak' from affecting camera movement in Freecam.
///
/// Also prevent the logical 'toggle sneak' state from updating while Freecam has input control.
/// Otherwise, the player can change sneak state when Freecam restores control back to the player.
@Mixin(KeyMapping.class)
abstract class KeyMappingMixin implements KeyMappingStateAccessor, KeyMappingInternalAccessor {

    // Store the effective key state separately from its 'toggle' state
    @Unique
    private boolean freecam$isKeyDown = false;

    @Unique
    @Override
    public boolean freecam$isKeyDown() {
        return freecam$isKeyDown;
    }

    @Unique
    @Override
    public void freecam$setKeyDown(boolean state) {
        freecam$isKeyDown = state;
    }

    // Capture key release separately from logical toggle state.
    @Inject(method = "release", at = @At("HEAD"))
    private void onRelease(CallbackInfo ci) {
        freecam$setKeyDown(false);
    }

    /// Capture effective input state during in-game focus update sync.
    ///
    /// Rather than handling normal input, [KeyMapping#setAll()] updates keys when [focusing 'in game'][KeyMapping#shouldSetOnIngameFocus()].
    /// Therefore, we should not suppress 'toggle sneak' like we do in [handleOnSetDown][#freecam$handleOnSetDown(KeyMapping, boolean, Consumer)].
    @WrapOperation(method = "setAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V"))
    private static void onSetAll_setDown(KeyMapping instance, boolean state, Operation<Void> original) {
        ((KeyMappingInternalAccessor) instance).freecam$setKeyDown(state);
        original.call(instance, state);
    }

    /// Capture effective input state at the boundary where input events update [KeyMapping] state.
    /// @see #freecam$handleOnSetDown(KeyMapping, boolean, Consumer) version-agnostic implementation
    //? if >= 1.21.11 {
    @Group(name = "freecam$onSet_forAllKeyMappings", min = 1, max = 1)
    @WrapOperation(method = "set", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;forAllKeyMappings(Lcom/mojang/blaze3d/platform/InputConstants$Key;Ljava/util/function/Consumer;)V"))
    private static void onSet_forAllKeyMappings_vanilla(InputConstants.Key key, Consumer<KeyMapping> consumer, Operation<Void> original, @Local(argsOnly = true) boolean state) {
        original.call(key, (Consumer<KeyMapping>) mapping -> freecam$handleOnSetDown(mapping, state, consumer));
    }

    /// NeoForge-specific variant of [#onSet_forAllKeyMappings_vanilla(InputConstants.Key, Consumer, Operation, boolean)].
    /// NeoForge's `set()` calls their `forAllKeyMappings` overload, which has an additional `releasing` parameter.
    @Group(name = "freecam$onSet_forAllKeyMappings", min = 1, max = 1)
    @WrapOperation(method = "set", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;forAllKeyMappings(Lcom/mojang/blaze3d/platform/InputConstants$Key;Ljava/util/function/Consumer;Z)V"))
    @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
    private static void onSet_forAllKeyMappings_neoforge(InputConstants.Key key, Consumer<KeyMapping> consumer, boolean releasing, Operation<Void> original, @Local(argsOnly = true) boolean state) {
        original.call(key, (Consumer<KeyMapping>) mapping -> freecam$handleOnSetDown(mapping, state, consumer), releasing);
    }
    //? } else {
    /*@WrapOperation(method = "set", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V"))
    private static void onSet_setDown(KeyMapping instance, boolean state, Operation<Void> original) {
        freecam$handleOnSetDown(instance, state, mapping -> original.call(mapping, state));
    }
    *///? }

    /// Store the effective key `state` for `mapping`, then invoke `callback`.
    /// When `mapping` is the sneak key, `callback` is **not** invoked while Freecam has input control.
    @Unique
    private static void freecam$handleOnSetDown(KeyMapping mapping, boolean state, Consumer<KeyMapping> callback) {
        ((KeyMappingInternalAccessor) mapping).freecam$setKeyDown(state);
        if (mapping == MC.options.keyShift && Freecam.isEnabled() && !Freecam.isPlayerControlEnabled()) return;
        callback.accept(mapping);
    }
}
