package net.xolt.freecam.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.xolt.freecam.mixins.KeyMappingAccessor;

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
        if (isSneakKeyDown(freeCamera)) {
            velocityY -= vSpeed;
        }

        freeCamera.setDeltaMovement(velocityX, velocityY, velocityZ);
    }

    // The sneak keybind can be set to toggle rather than hold (Options > Controls > Toggle Sneak).
    // In that case, MC.options.keyShift.isDown() reflects whatever sneak state the player toggled
    // to beforehand, not whether the key is currently held, causing the camera to drift down for as
    // long as that stale toggle happens to be on. Poll the physical key state directly instead, so
    // descending in freecam always requires actually holding the key down, regardless of the toggle
    // sneak setting.
    private static boolean isSneakKeyDown(FreeCamera freeCamera) {
        //~ if >=26.2 screen -> 'gui.screen()'
        if (MC.gui.screen() != null) {
            return false;
        }

        InputConstants.Key key = ((KeyMappingAccessor) MC.options.keyShift).freecam$getKey();
        if (key.getType() != InputConstants.Type.KEYSYM && key.getType() != InputConstants.Type.SCANCODE) {
            // Not bound to a keyboard key (e.g. a mouse button); fall back to the mapping's own state.
            return freeCamera.input.keyPresses.shift();
        }

        return InputConstants.isKeyDown(MC.getWindow(), key.getValue());
    }
}
