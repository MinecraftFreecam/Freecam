package net.xolt.freecam.util;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.xolt.freecam.Freecam;

import java.util.UUID;

public class ClonePlayerEntity extends OtherClientPlayerEntity {

    public ClonePlayerEntity(ClientWorld world, PlayerEntity player) {
        super(world, player.getGameProfile());

        copyFrom(player);
        updateInventory();
        getAttributes().setFrom(player.getAttributes());

        setPose(player.getPose());
        setBodyYaw(player.bodyYaw);
        limbAngle = player.limbAngle;
        limbDistance = player.limbDistance;
        resetCapeMovement();

        getPlayerListEntry();
        dataTracker.set(PLAYER_MODEL_PARTS, player.getDataTracker().get(PLAYER_MODEL_PARTS));
        setUuid(UUID.randomUUID());
    }

    public void updateInventory() {
        getInventory().clone(Freecam.MC.player.getInventory());
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

    private void resetCapeMovement() {
        capeX = getX();
        capeY = getY();
        capeZ = getZ();
    }
}
