package net.xolt.freecam.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.ChunkPos;

public class FreecamPosition {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public EntityPose pose;

    public FreecamPosition(Entity entity) {
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        pitch = entity.getPitch();
        yaw = entity.getYaw();
        pose = entity.getPose();
    }

    public static FreecamPosition getSwimmingPosition(Entity entity) {
        FreecamPosition position = new FreecamPosition(entity);

        // Set pose to swimming, adjusting y position so eye-height doesn't change
        if (position.pose != EntityPose.SWIMMING) {
            position.y += entity.getEyeHeight(position.pose) - entity.getEyeHeight(EntityPose.SWIMMING);
            position.pose = EntityPose.SWIMMING;
        }

        return position;
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos((int) (x / 16), (int) (z / 16));
    }
}
