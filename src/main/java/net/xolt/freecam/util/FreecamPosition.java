package net.xolt.freecam.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.ChunkPos;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FreecamPosition {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public EntityPose pose;

    private final Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
    private final Vector3f verticalPlane = new Vector3f(0.0F, 1.0F, 0.0F);
    private final Vector3f diagonalPlane = new Vector3f(1.0F, 0.0F, 0.0F);
    private final Vector3f horizontalPlane = new Vector3f(0.0F, 0.0F, 1.0F);

    public FreecamPosition(Entity entity) {
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        pose = entity.getPose();
        setRotation(entity.getYaw(), entity.getPitch());
    }

    // From net.minecraft.client.render.Camera.setRotation
    protected void setRotation(float yaw, float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
        rotation.rotationYXZ(-yaw * ((float) Math.PI / 180), pitch * ((float) Math.PI / 180), 0.0f);
        horizontalPlane.set(0.0f, 0.0f, 1.0f).rotate(rotation);
        verticalPlane.set(0.0f, 1.0f, 0.0f).rotate(rotation);
        diagonalPlane.set(1.0f, 0.0f, 0.0f).rotate(rotation);
    }

    // Invert the rotation so that it is mirrored
    // As-per net.minecraft.client.render.Camera.update
    public void mirrorRotation() {
        setRotation(yaw + 180.0F, -pitch);
    }

    // Move forward/backward relative to the current rotation
    public void moveForward(double distance) {
        move(distance, 0, 0);
    }

    // Move up/down relative to the current rotation
    public void moveUp(double distance) {
        move(0, distance, 0);
    }

    // Move right/left relative to the current rotation
    public void moveRight(double distance) {
        move(0, 0, distance);
    }

    // Move relative to current rotation
    // From net.minecraft.client.render.Camera.moveBy
    public void move(double fwd, double up, double right) {
        x += (double) horizontalPlane.x() * fwd
           + (double) verticalPlane.x()   * up
           + (double) diagonalPlane.x()   * right;
        
        y += (double) horizontalPlane.y() * fwd
           + (double) verticalPlane.y()   * up
           + (double) diagonalPlane.y()   * right;
        
        z += (double) horizontalPlane.z() * fwd
           + (double) verticalPlane.z()   * up
           + (double) diagonalPlane.z()   * right;
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos((int) (x / 16), (int) (z / 16));
    }
}
