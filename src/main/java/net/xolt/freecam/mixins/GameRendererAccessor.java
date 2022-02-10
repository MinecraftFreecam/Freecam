package net.xolt.freecam.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("renderHand")
    void setRenderHand(boolean renderHand);

    @Accessor("client") MinecraftClient getClient();
}
