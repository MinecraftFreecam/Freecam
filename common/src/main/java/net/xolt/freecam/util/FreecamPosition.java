package net.xolt.freecam.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ChunkPos;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.tripod.TripodSlot;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Supplier;

import static net.xolt.freecam.Freecam.MC;

public class FreecamPosition {
    public double x;
    public double y;
    public double z;
    public ModConfig.Perspective perspective;
    public float pitch;
    public float yaw;

    private Supplier<Component> nameSupplier = null;
    private final Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
    private final Vector3f verticalPlane = new Vector3f(0.0F, 1.0F, 0.0F);
    private final Vector3f diagonalPlane = new Vector3f(1.0F, 0.0F, 0.0F);
    private final Vector3f horizontalPlane = new Vector3f(0.0F, 0.0F, 1.0F);

    private FreecamPosition(double x, double y, double z, float yaw, float pitch, ModConfig.Perspective perspective) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.perspective = perspective;
        setRotation(yaw, pitch);
    }

    public static FreecamPosition defaultPosition() {
        return initialPerspectiveOf(MC.player);
    }

    public static FreecamPosition initialPerspectiveOf(Entity entity) {
        return of(entity, ModConfig.get().visual.perspective);
    }

    public static FreecamPosition of(Entity entity) {
        return of(entity, ModConfig.Perspective.INSIDE);
    }

    public static FreecamPosition of(Entity entity, ModConfig.Perspective perspective) {
        FreecamPosition position = new FreecamPosition(entity.getX(), getSwimmingY(entity), entity.getZ(), entity.getYRot(), entity.getXRot(), perspective);
        position.setNameSupplier(() -> entity.getName().plainCopy());
        return position;
    }

    public static FreecamPosition of(TripodSlot tripod) {
        FreecamPosition position = Optional.ofNullable(Freecam.getTripod(tripod)).orElseGet(FreecamPosition::defaultPosition);
        position.setNameSupplier(() -> Component.literal(tripod.toString()));
        return position;
    }

    public static FreecamPosition copyOf(FreecamPosition position) {
        return new FreecamPosition(position.x, position.y, position.z, position.yaw, position.pitch, position.perspective);
    }

    // From net.minecraft.client.render.Camera.setRotation
    public void setRotation(float yaw, float pitch) {
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

    public boolean isInRange() {
        ChunkPos chunk = getChunkPos();
        return MC.level.getChunkSource().hasChunk(chunk.x, chunk.z);
    }

    public Component getName() {
        return Optional.ofNullable(nameSupplier)
                .map(Supplier::get)
                .orElseGet(() -> Component.translatable("msg.freecamPosition.coords", x, y, z));
    }

    public void setNameSupplier(Supplier<Component> supplier) {
        this.nameSupplier = supplier;
    }

    public void setName(Component name) {
        this.nameSupplier = () -> name;
    }

    private static double getSwimmingY(Entity entity) {
        if (entity.getPose() == Pose.SWIMMING) {
            return entity.getY();
        }
        return entity.getY() - entity.getEyeHeight(Pose.SWIMMING) + entity.getEyeHeight(entity.getPose());
    }
}
