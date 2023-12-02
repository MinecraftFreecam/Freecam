package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Unique private float freecam$tickDelta;

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyVariable(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"), argsOnly = true)
    private ClientPlayerEntity onRenderItem(ClientPlayerEntity player) {
        if (Freecam.isEnabled()) {
            return Freecam.getFreeCamera();
        }
        return player;
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"))
    private void storeTickDelta(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        this.freecam$tickDelta = tickDelta;
    }

    // Makes arm shading depend upon FreeCamera position rather than player position.
    @ModifyVariable(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"), argsOnly = true)
    private int onRenderItem2(int light) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getLight(Freecam.getFreeCamera(), freecam$tickDelta);
        }
        return light;
    }
}
