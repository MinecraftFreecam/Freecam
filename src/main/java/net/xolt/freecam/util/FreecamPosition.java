package net.xolt.freecam.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class FreecamPosition {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public EntityPose pose;

    private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private final Vec3f verticalPlane = new Vec3f(0.0F, 1.0F, 0.0F);
    private final Vec3f diagonalPlane = new Vec3f(1.0F, 0.0F, 0.0F);
    private final Vec3f horizontalPlane = new Vec3f(0.0F, 0.0F, 1.0F);

    public FreecamPosition(Entity entity) {

        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        pose = entity.getPose();
        setRotation(entity.getYaw(), entity.getPitch());
    }

    // From net.minecraft.client.render.Camera.setRotation
    public void setRotation(float yaw, float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
        this.rotation.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        this.rotation.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        this.horizontalPlane.set(0.0F, 0.0F, 1.0F);
        this.horizontalPlane.rotate(this.rotation);
        this.verticalPlane.set(0.0F, 1.0F, 0.0F);
        this.verticalPlane.rotate(this.rotation);
        this.diagonalPlane.set(1.0F, 0.0F, 0.0F);
        this.diagonalPlane.rotate(this.rotation);
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
        x += (double) horizontalPlane.getX() * fwd
           + (double) verticalPlane.getX()   * up
           + (double) diagonalPlane.getX()   * right;

        y += (double) horizontalPlane.getY() * fwd
           + (double) verticalPlane.getY()   * up
           + (double) diagonalPlane.getY()   * right;

        z += (double) horizontalPlane.getZ() * fwd
           + (double) verticalPlane.getZ()   * up
           + (double) diagonalPlane.getZ()   * right;
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
