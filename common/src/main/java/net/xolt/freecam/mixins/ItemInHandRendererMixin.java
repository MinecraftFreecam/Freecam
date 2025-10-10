package net.xolt.freecam.mixins;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Unique private float freecam$tickDelta;

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private LocalPlayer onRenderItem(LocalPlayer player) {
        if (Freecam.isEnabled()) {
            return Freecam.getFreeCamera();
        }
        return player;
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
