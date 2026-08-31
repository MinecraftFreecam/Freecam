package net.xolt.freecam.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.ToggleKeyMapping;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ToggleKeyMapping.class)
public class ToggleKeyMappingMixin {

    @Shadow @Final private BooleanSupplier needsToggle;

    // Prevents toggle sneak from flipping the player's crouch state while the sneak key
    // moves the camera (see Motion.isSneakKeyDown). Mouse bindings are left untouched.
    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void onSetDown(boolean down, CallbackInfo ci) {
        if (down && needsToggle.getAsBoolean()
                && (Object) this == MC.options.keyShift
                && MC.options.keyShift.key.getType() != InputConstants.Type.MOUSE
                && Freecam.isEnabled() && !Freecam.isPlayerControlEnabled()) {
            ci.cancel();
        }
    }
}
