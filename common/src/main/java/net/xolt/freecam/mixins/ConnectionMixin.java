package net.xolt.freecam.mixins;

import net.minecraft.network.Connection;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

    // Disables freecam if the player disconnects.
    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    private void onHandleDisconnection(CallbackInfo ci) {
        Freecam.onDisconnect();
    }
}
