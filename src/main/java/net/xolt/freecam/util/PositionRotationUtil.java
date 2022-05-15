package net.xolt.freecam.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class PositionRotationUtil {

    private final Vec3f verticalPlane = new Vec3f(0.0F, 1.0F, 0.0F);
    private final Vec3f diagonalPlane = new Vec3f(1.0F, 0.0F, 0.0F);
    private final Vec3f horizontalPlane = new Vec3f(0.0F, 0.0F, 1.0F);
    private Vec3d position = Vec3d.ZERO;
    private float pitch = 0;
    private float yaw = 0;


    public PositionRotationUtil(double x, double y, double z, float yaw, float pitch) {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    public PositionRotationUtil(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public PositionRotationUtil(float yaw, float pitch) {
        this(0, 0, 0, yaw, pitch);
    }

    public PositionRotationUtil() {
        this(0, 0, 0, 0, 0);
    }

    public PositionRotationUtil(Entity entity) {
        this();
        copyPositionAndRotation(entity);
    }

    public void copyPositionAndRotation(Entity entity) {
        setPosition(entity.getX(), entity.getY(), entity.getZ());
        setRotation(entity.getYaw(), entity.getPitch());
    }

    public void applyPositionAndRotation(Entity entity) {
        entity.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
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
        double xShift = (double) horizontalPlane.getX() * fwd + (double) verticalPlane.getX() * up + (double) diagonalPlane.getX() * right;
        double yShift = (double) horizontalPlane.getY() * fwd + (double) verticalPlane.getY() * up + (double) diagonalPlane.getY() * right;
        double zShift = (double) horizontalPlane.getZ() * fwd + (double) verticalPlane.getZ() * up + (double) diagonalPlane.getZ() * right;
        setPosition(getX() + xShift, getY() + yShift, getZ() + zShift);
    }

    public void setPosition(double x, double y, double z) {
        position = new Vec3d(x, y, z);
    }

    // Invert the rotation so it is mirrored
    // As-per net.minecraft.client.render.Camera.update
    public void mirrorRotation() {
        setRotation(yaw + 180.0F, -pitch);
    }

    // Set the rotation angles
    // From net.minecraft.client.render.Camera.setRotation
    public void setRotation(float yaw, float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;

        // If this was being run often, we'd want to store rotation in a
        // final-field, to be more memory efficient.
        Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
        rotation.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        rotation.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));

        horizontalPlane.set(0.0F, 0.0F, 1.0F);
        horizontalPlane.rotate(rotation);

        verticalPlane.set(0.0F, 1.0F, 0.0F);
        verticalPlane.rotate(rotation);

        diagonalPlane.set(1.0F, 0.0F, 0.0F);
        diagonalPlane.rotate(rotation);
    }

    public double getX() {
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    public double getZ() {
        return position.z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Vec3d getPosition() {
        return new Vec3d(getX(), getY(), getZ());
    }

    public BlockPos getBlockPos() {
        return new BlockPos(getX(), getY(), getZ());
    }
}
