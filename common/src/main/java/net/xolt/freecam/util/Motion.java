package net.xolt.freecam.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Motion {

    public static final double DIAGONAL_MULTIPLIER = Mth.sin((float) Math.toRadians(45));

    public static void doMotion(FreeCamera freeCamera, double hSpeed, double vSpeed) {
        float yaw = freeCamera.getYRot();
        double velocityX = 0.0;
        double velocityY = 0.0;
        double velocityZ = 0.0;

        Vec3 forward = Vec3.directionFromRotation(0, yaw);
        Vec3 side = Vec3.directionFromRotation(0, yaw + 90);

        freeCamera.input.tick(false);
        hSpeed = hSpeed * (freeCamera.isSprinting() ? 1.5 : 1.0);

        boolean straight = false;
        if (freeCamera.input.up) {
            velocityX += forward.x * hSpeed;
            velocityZ += forward.z * hSpeed;
            straight = true;
        }
        if (freeCamera.input.down) {
            velocityX -= forward.x * hSpeed;
            velocityZ -= forward.z * hSpeed;
            straight = true;
        }

        boolean strafing = false;
        if (freeCamera.input.right) {
            velocityZ += side.z * hSpeed;
            velocityX += side.x * hSpeed;
            strafing = true;
        }
        if (freeCamera.input.left) {
            velocityZ -= side.z * hSpeed;
            velocityX -= side.x * hSpeed;
            strafing = true;
        }

        if (straight && strafing) {
            velocityX *= DIAGONAL_MULTIPLIER;
            velocityZ *= DIAGONAL_MULTIPLIER;
        }

        if (freeCamera.input.jumping) {
            velocityY += vSpeed;
        }
        if (freeCamera.input.shiftKeyDown) {
            velocityY -= vSpeed;
        }

        freeCamera.setDeltaMovement(velocityX, velocityY, velocityZ);
    }
}
