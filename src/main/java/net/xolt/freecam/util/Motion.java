package net.xolt.freecam.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Motion {

    public static final double DIAGONAL_MULTIPLIER = MathHelper.sin((float) Math.toRadians(45));

    public static void doMotion(FreeCamera freeCamera, double hSpeed, double vSpeed) {
        float yaw = freeCamera.getYaw();

        double velocityX = 0.0;
        double velocityY = 0.0;
        double velocityZ = 0.0;

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d side = Vec3d.fromPolar(0, yaw + 90);

        boolean straight = false;
        if(freeCamera.input.pressingForward) {
            velocityX += forward.x * hSpeed;
            velocityZ += forward.z * hSpeed;
            straight = true;
        }
        if(freeCamera.input.pressingBack) {
            velocityX -= forward.x * hSpeed;
            velocityZ -= forward.z * hSpeed;
            straight = true;
        }

        boolean strafing = false;
        if(freeCamera.input.pressingRight) {
            velocityZ += side.z * hSpeed;
            velocityX += side.x * hSpeed;
            strafing = true;
        }
        if(freeCamera.input.pressingLeft) {
            velocityZ -= side.z * hSpeed;
            velocityX -= side.x * hSpeed;
            strafing = true;
        }

        if (straight && strafing) {
            velocityX *= DIAGONAL_MULTIPLIER;
            velocityZ *= DIAGONAL_MULTIPLIER;
        }

        if(freeCamera.input.jumping) {
            velocityY += vSpeed;
        } else if(freeCamera.input.sneaking) {
            velocityY -= vSpeed;
        }

        freeCamera.setPosition(freeCamera.getX() + velocityX, freeCamera.getY() + velocityY, freeCamera.getZ() + velocityZ);
    }
}
