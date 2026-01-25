package net.xolt.freecam.mixins;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Unique private float freecam$tickDelta;

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyArgs(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private void onRenderHandsXBob(Args args) {
        if (!Freecam.isEnabled())
            return;

        float partialTick = args.get(0);
        args.set(1, Freecam.getFreeCamera().xBobO);
        args.set(2, Freecam.getFreeCamera().xBob);
    }

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyArgs(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 1))
    private void onRenderHandsYBob(Args args) {
        if (!Freecam.isEnabled())
            return;

        float partialTick = args.get(0);
        args.set(1, Freecam.getFreeCamera().yBobO);
        args.set(2, Freecam.getFreeCamera().yBob);
    }

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void storeTickDelta(float partialTick, PoseStack poseStack, SubmitNodeCollector nodeCollector, LocalPlayer player, int packedLight, CallbackInfo ci) {
        this.freecam$tickDelta = partialTick;
    }

    // Makes arm shading depend upon FreeCamera position rather than player position.
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private int onRenderItem2(int light) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(Freecam.getFreeCamera(), freecam$tickDelta);
        }
        return light;
    }
}
