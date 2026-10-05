package net.xolt.freecam.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.accessors.KeyMappingStateAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
abstract class KeyboardInputMixin {

    @Final @Shadow private Options options;

    /// When controlling the camera, use the [effective sneak key state][KeyMappingStateAccessor#freecam$isKeyDown()]
    /// instead of [Minecraft's state][KeyMapping#isDown()], which may actually represent 'toggle sneak'.
    @Expression("this.options.keyShift.isDown()")
    @Definition(id = "options", field = "Lnet/minecraft/client/player/KeyboardInput;options:Lnet/minecraft/client/Options;")
    @Definition(id = "keyShift", field = "Lnet/minecraft/client/Options;keyShift:Lnet/minecraft/client/KeyMapping;")
    @Definition(id = "isDown", method = "Lnet/minecraft/client/KeyMapping;isDown()Z")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean modifyShiftKeyDown(boolean original) {
        return Freecam.isEnabled() && !Freecam.isPlayerControlEnabled()
            ? ((KeyMappingStateAccessor) options.keyShift).freecam$isKeyDown()
            : original;
    }
}
