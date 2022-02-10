package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;
import net.xolt.freecam.config.ModConfig;

import java.util.UUID;

import static net.xolt.freecam.Freecam.MC;

public class FreeCamera extends ClientPlayerEntity {

    private static final ClientPlayNetworkHandler NETWORK_HANDLER = new ClientPlayNetworkHandler(MC, MC.currentScreen, MC.getNetworkHandler().getConnection(), new GameProfile(UUID.randomUUID(), "FreeCamera")) {
        @Override
        public void sendPacket(Packet<?> packet) {
        }
    };

    public FreeCamera() {
        super(MC, MC.world, NETWORK_HANDLER, MC.player.getStatHandler(), MC.player.getRecipeBook(), false, false);

        copyPositionAndRotation(MC.player);
        this.getAbilities().allowModifyWorld = ModConfig.INSTANCE.allowBlockBreak;
        this.noClip = true;
        this.input = new KeyboardInput(MC.options);
    }

    public void spawn() {
        if (clientWorld != null) {
            clientWorld.addEntity(getId(), this);
        }
    }

    public void despawn() {
        if (clientWorld != null && clientWorld.getEntityById(getId()) != null) {
            clientWorld.removeEntity(getId(), RemovalReason.DISCARDED);
        }
    }

    @Override
    public void tickMovement() {
        input.tick(false);
        Motion.doMotion(this, ModConfig.INSTANCE.horizontalSpeed, ModConfig.INSTANCE.verticalSpeed);
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public void setPose(EntityPose pose) {
    }

    @Override
    public boolean hasStatusEffect(StatusEffect effect) {
        return MC.player.hasStatusEffect(effect);
    }

    @Override
    public StatusEffectInstance getStatusEffect(StatusEffect effect) {
        return MC.player.getStatusEffect(effect);
    }

    @Override
    protected Box calculateBoundingBox() {
        return new Box(0D, 0D, 0D, 0D, 0D, 0D);
    }

}
