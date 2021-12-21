package net.xolt.freecam.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.xolt.freecam.Freecam.MC;

public class Motion {

    public static final double DIAGONAL_MULTIPLIER = MathHelper.sin((float) Math.toRadians(45));

    public static void doMotion(double hSpeed, double vSpeed) {
        ClientPlayerEntity player = MC.player;
        float yaw = player.getYaw();

        double velocityX = 0.0;
        double velocityY = 0.0;
        double velocityZ = 0.0;

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d side = Vec3d.fromPolar(0, yaw + 90);

        boolean straight = false;
        if(player.input.pressingForward) {
            velocityX += forward.x * hSpeed;
            velocityZ += forward.z * hSpeed;
            straight = true;
        }
        if(player.input.pressingBack) {
            velocityX -= forward.x * hSpeed;
            velocityZ -= forward.z * hSpeed;
            straight = true;
        }

        boolean strafing = false;
        if(player.input.pressingRight) {
            velocityZ += side.z * hSpeed;
            velocityX += side.x * hSpeed;
            strafing = true;
        }
        if(player.input.pressingLeft) {
            velocityZ -= side.z * hSpeed;
            velocityX -= side.x * hSpeed;
            strafing = true;
        }

        if (straight && strafing) {
            velocityX *= DIAGONAL_MULTIPLIER;
            velocityZ *= DIAGONAL_MULTIPLIER;
        }

        if(player.input.jumping) {
            velocityY += vSpeed;
        } else if(player.input.sneaking) {
            velocityY -= vSpeed;
        }

        player.setVelocity(velocityX, velocityY, velocityZ);
    }
}
