package net.xolt.freecam.util;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class ClonePlayerEntity extends OtherClientPlayerEntity {
    public ClonePlayerEntity(ClientWorld world, PlayerEntity player) {
        super(world, player.getGameProfile());

        copyFrom(player);
        getInventory().clone(player.getInventory());
        getAttributes().setFrom(player.getAttributes());
        resetCapeMovement();

        getPlayerListEntry();
        dataTracker.set(PLAYER_MODEL_PARTS, player.getDataTracker().get(PLAYER_MODEL_PARTS));
        setUuid(UUID.randomUUID());
    }

    private void resetCapeMovement() {
        capeX = getX();
        capeY = getY();
        capeZ = getZ();
    }
}
