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
        pitch = entity.pitch;
        yaw = entity.yaw;
        pose = entity.getPose();
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos((int) (x / 16), (int) (z / 16));
    }
}
