package net.xolt.freecam.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import static net.xolt.freecam.Freecam.MC;

public class Motion {

    public static final double DIAGONAL_MULTIPLIER = Mth.sin((float) Math.toRadians(45));

    public static void doMotion(FreeCamera freeCamera, double hSpeed, double vSpeed) {
        float yaw = freeCamera.getYRot();
        double velocityX = 0.0;
        double velocityY = 0.0;
        double velocityZ = 0.0;

        Vec3 forward = Vec3.directionFromRotation(0, yaw);
        Vec3 side = Vec3.directionFromRotation(0, yaw + 90);

        hSpeed = hSpeed * (freeCamera.isSprinting() ? 1.5 : 1.0);

        boolean straight = false;
        if (freeCamera.input.keyPresses.forward()) {
            velocityX += forward.x * hSpeed;
            velocityZ += forward.z * hSpeed;
            straight = true;
        }
        if (freeCamera.input.keyPresses.backward()) {
            velocityX -= forward.x * hSpeed;
            velocityZ -= forward.z * hSpeed;
            straight = true;
        }

        boolean strafing = false;
        if (freeCamera.input.keyPresses.right()) {
            velocityZ += side.z * hSpeed;
            velocityX += side.x * hSpeed;
            strafing = true;
        }
        if (freeCamera.input.keyPresses.left()) {
            velocityZ -= side.z * hSpeed;
            velocityX -= side.x * hSpeed;
            strafing = true;
        }

        if (straight && strafing) {
            velocityX *= DIAGONAL_MULTIPLIER;
            velocityZ *= DIAGONAL_MULTIPLIER;
        }

        if (freeCamera.input.keyPresses.jump()) {
            velocityY += vSpeed;
        }
        if (isSneakKeyDown()) {
            velocityY -= vSpeed;
        }

        freeCamera.setDeltaMovement(velocityX, velocityY, velocityZ);
    }

    // With Toggle Sneak enabled, keyShift.isDown() reflects the toggled crouch state, not whether
    // the key is held. Poll the physical key instead so descending always requires holding it.
    static boolean isSneakKeyDown() {
        //~ if >=26.2 screen -> 'gui.screen()'
        if (MC.gui.screen() != null) {
            return false;
        }

        InputConstants.Key key = MC.options.keyShift.key;
        if (key.getType() == InputConstants.Type.MOUSE) {
            // Mouse buttons can't be polled the same way; use the mapping's own state.
            return MC.options.keyShift.isDown();
        }

        //? if >=26.3 {
        return InputConstants.isKeyDown(key.getValue());
        //? } else {
        /*//~ if <1.21.11 'MC.getWindow()' -> 'MC.getWindow().getWindow()'
        return InputConstants.isKeyDown(MC.getWindow(), key.getValue());
        *///? }
    }
}
