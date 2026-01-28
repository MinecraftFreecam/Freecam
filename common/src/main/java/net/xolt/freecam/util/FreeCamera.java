package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec2;
import net.xolt.freecam.config.ModConfig;
import org.jetbrains.annotations.ApiStatus;
//? if >=1.21.11 {
import net.minecraft.client.player.ClientInput;
//? } else {
/*import net.minecraft.client.player.Input;
*///? }
//? if >=1.20.6 {
import net.minecraft.core.Holder;
import net.minecraft.client.multiplayer.ClientLevel;
//? }

import java.util.UUID;

import static net.xolt.freecam.Freecam.MC;

@ApiStatus.Internal
@ApiStatus.AvailableSince("0.4.0")
public class FreeCamera extends AbstractClientPlayer {
    //? if >=1.21.11 {
    public ClientInput input;
    //? } else
    //public Input input;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;

    public FreeCamera(int id) {
        super(MC.level, new GameProfile(UUID.randomUUID(), "FreeCamera"));

        setId(id);
        setPose(Pose.SWIMMING);
        getAbilities().flying = true;
        input = new KeyboardInput(MC.options);
    }

    @Override
    public void tick() {
        input.tick(
            //? if <1.21.11
            //false
            //? if <1.21.11 && > 1.18.2
            //, 0.3F
        );
        doMotion();
        super.tick();
    }

    @Override
    public void copyPosition(Entity entity) {
        applyPosition(new FreecamPosition(entity));
    }

    public void applyPosition(FreecamPosition position) {
        //? if >=1.21.11 {
        snapTo(
        //? } else
        //moveTo(
                position.x, position.y, position.z, position.yaw, position.pitch
        );
        xBob = getXRot();
        yBob = getYRot();
        xBobO = xBob; // Prevents camera from rotating upon entering freecam.
        yBobO = yBob;
    }

    // Mutate the position and rotation based on perspective
    // If checkCollision is true, move as far as possible without colliding
    public void applyPerspective(ModConfig.Perspective perspective, boolean checkCollision) {
        FreecamPosition position = new FreecamPosition(this);

        switch (perspective) {
            case INSIDE:
                // No-op
                break;
            case FIRST_PERSON:
                // Move just in front of the player's eyes
                moveForwardUntilCollision(position, 0.4, checkCollision);
                break;
            case THIRD_PERSON_MIRROR:
                // Invert the rotation and fallthrough into the THIRD_PERSON case
                position.mirrorRotation();
            case THIRD_PERSON:
                // Move back as per F5 mode
                moveForwardUntilCollision(position, -4.0, checkCollision);
                break;
        }
    }

    // Move FreeCamera forward using FreecamPosition.moveForward.
    // If checkCollision is true, stop moving forward before hitting a collision.
    // Return true if successfully able to move.
    private boolean moveForwardUntilCollision(FreecamPosition position, double distance, boolean checkCollision) {
        if (!checkCollision) {
            position.moveForward(distance);
            applyPosition(position);
            return true;
        }
        return moveForwardUntilCollision(position, distance);
    }

    // Same as above, but always check collision.
    private boolean moveForwardUntilCollision(FreecamPosition position, double maxDistance) {
        boolean negative = maxDistance < 0;
        maxDistance = negative ? -1 * maxDistance : maxDistance;
        double increment = 0.1;

        // Move forward by increment until we reach maxDistance or hit a collision
        for (double distance = 0.0; distance < maxDistance; distance += increment) {
            FreecamPosition oldPosition = new FreecamPosition(this);

            position.moveForward(negative ? -1 * increment : increment);
            applyPosition(position);

            if (!wouldNotSuffocateAtTargetPose(getPose())) {
                // Revert to last non-colliding position and return whether we were unable to move at all
                applyPosition(oldPosition);
                return distance > 0;
            }
        }

        return true;
    }

    public void spawn() {
        //? if >=1.20.6 {
        ((ClientLevel) level()).addEntity(this);
        //? } else
        //clientLevel.putNonPlayerEntity(getId(), this);
    }

    public void despawn() {
        //? if >=1.20.6 {
        ((ClientLevel) level()).removeEntity(getId(), RemovalReason.DISCARDED);
        //? } else
        //clientLevel.removeEntity(getId(), RemovalReason.DISCARDED);
    }

    // Prevents fall damage sound when FreeCamera touches ground with noClip disabled.
    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    // Needed for hand swings to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
    @Override
    public float getAttackAnim(float tickDelta) {
        return MC.player.getAttackAnim(tickDelta);
    }

    // Needed for item use animations to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
    @Override
    public int getUseItemRemainingTicks() {
        return MC.player.getUseItemRemainingTicks();
    }

    // Also needed for item use animations to be shown in freecam.
    @Override
    public boolean isUsingItem() {
        return MC.player.isUsingItem();
    }

    // Prevents slow down from ladders/vines.
    @Override
    public boolean onClimbable() {
        return false;
    }

    // Prevents slow down from water.
    @Override
    public boolean isInWater() {
        return false;
    }

    // Makes night vision apply to FreeCamera when Iris is enabled.
    @Override
    public MobEffectInstance getEffect(
            //? if >=1.20.6 {
            Holder<MobEffect> effect
            //? } else
            //MobEffect effect
    ) {
        return MC.player.getEffect(effect);
    }

    // Prevents pistons from moving FreeCamera when collision.ignoreAll is enabled.
    @Override
    public PushReaction getPistonPushReaction() {
        return ModConfig.INSTANCE.collision.ignoreAll ? PushReaction.IGNORE : PushReaction.NORMAL;
    }

    // Prevents collision with solid entities (shulkers, boats)
    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    // Ensures that the FreeCamera is always in the swimming pose.
    @Override
    public void setPose(Pose pose) {
        super.setPose(Pose.SWIMMING);
    }

    // Prevents water submersion sounds from playing.
    @Override
    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    // Prevents water submersion sounds from playing.
    @Override
    protected void doWaterSplashEffect() {}

    private void doMotion() {
        if (ModConfig.INSTANCE.movement.flightMode.equals(ModConfig.FlightMode.DEFAULT)) {
            getAbilities().setFlyingSpeed(0);
            Motion.doMotion(this, ModConfig.INSTANCE.movement.horizontalSpeed, ModConfig.INSTANCE.movement.verticalSpeed);
        } else {
            getAbilities().setFlyingSpeed((float) ModConfig.INSTANCE.movement.verticalSpeed / 10);

            if (this.input.keyPresses.shift() ^ this.input.keyPresses.jump()) {
                int direction = this.input.keyPresses.jump() ? 1 : -1;
                this.setDeltaMovement(this.getDeltaMovement().add(0.0F, ((float) direction * this.getAbilities().getFlyingSpeed() * 3.0F), 0.0F));
            }
        }
        getAbilities().flying = true;
        setOnGround(false);
    }

    @Override
    public float getViewXRot(float partialTick) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float partialTick) {
        return this.getYRot();
    }

    // In newer versions, this also enables movement ticking (like below)
    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    //? if >=1.21.11 {
    //In LivingEntity's aiStep(), this method decides whether to call travel(), enabling movement ticking
    @Override
    public boolean canSimulateMovement() {
        return true;
    }

    @Override
    protected void applyInput() {
        Vec2 vec2 = this.input.getMoveVector();
        if (vec2.lengthSquared() != 0.0F)
            vec2 = vec2.scale(0.98F);
        applyInputHelper(vec2, this.input.keyPresses.jump());
    }
    //? } else {
    /*@Override
    protected void serverAiStep() {
        Vec2 moveVector = new Vec2(this.input.keyPresses.left()Impulse, this.input.forwardImpulse);
        applyInputHelper(moveVector, this.input.keyPresses.jump());
    }
    *///? }

    private void applyInputHelper(Vec2 moveVector, boolean jumping) {
        this.xxa = moveVector.x;
        this.zza = moveVector.y;
        this.jumping = jumping;
        this.setSprinting((MC.options.keySprint.isDown() && this.input.keyPresses.forward()) || (this.input.keyPresses.forward() && this.isSprinting()));
        this.yBobO = this.yBob;
        this.xBobO = this.xBob;
        this.xBob = this.xBob + (this.getXRot() - this.xBob) * 0.5F;
        this.yBob = this.yBob + (this.getYRot() - this.yBob) * 0.5F;
    }
}
