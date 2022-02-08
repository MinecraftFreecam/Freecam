package net.xolt.freecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.ClonePlayerEntity;
import org.lwjgl.glfw.GLFW;

public class Freecam implements ClientModInitializer {

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    private static KeyBinding keyBinding;
    private static boolean enabled = false;

    private static float[] rot;
    private static float[] limbs;
    private static float flightSpeed;
    private static boolean isFlying;
    private static boolean isFallFlying;
    private static boolean canBreakBlocks;
    private static Vec3d pos;
    private static Entity riding;
    private static EntityPose pose;
    private static ClonePlayerEntity clone;

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.freecam.freecam"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                toggle();
            }
        });
    }

    public static void toggle() {
        if (enabled) {
            enabled = false;
            onDisable();
        } else {
            onEnable();
            enabled = true;
        }
    }

    private static void onEnable() {
        rot = new float[]{MC.player.getYaw(), MC.player.getPitch(), MC.player.bodyYaw};
        limbs = new float[]{MC.player.limbAngle, MC.player.limbDistance};
        flightSpeed = MC.player.getAbilities().getFlySpeed();
        isFlying = MC.player.getAbilities().flying;
        isFallFlying = MC.player.isFallFlying();
        canBreakBlocks = MC.player.getAbilities().allowModifyWorld;
        pos = MC.player.getPos();
        pose = MC.player.getPose();

        MC.chunkCullingEnabled = false;
        MC.player.setVelocity(Vec3d.ZERO);

        if (!ModConfig.INSTANCE.allowBlockBreak) {
            MC.player.getAbilities().allowModifyWorld = false;
        }

        if (MC.player.getVehicle() != null) {
            riding = MC.player.getVehicle();
            riding.removeAllPassengers();
        }

        if (MC.gameRenderer.getCamera().isThirdPerson()) {
            MC.gameRenderer.getClient().options.setPerspective(Perspective.FIRST_PERSON);
        }

        if (!ModConfig.INSTANCE.showHand) {
            MC.gameRenderer.setRenderHand(false);
        }

        if (ModConfig.INSTANCE.showClone) {
            clone = new ClonePlayerEntity(MC.world, MC.player);
            clone.spawn();
            if (riding != null) {
                clone.startRiding(riding);
            }
        }

        if (isFallFlying) {
            MC.player.stopFallFlying();
        }

        if (ModConfig.INSTANCE.freecamMode.equals(ModConfig.FlightMode.VANILLA)) {
            MC.player.getAbilities().setFlySpeed((float) (0.1f * ModConfig.INSTANCE.freecamSpeed));
        }

        if (MC.player.isSprinting()) {
            MC.player.networkHandler.sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        if (ModConfig.INSTANCE.notify) {
            MC.player.sendMessage(new LiteralText(ModConfig.INSTANCE.enableMessage), true);
        }
    }

    private static void onDisable() {
        MC.chunkCullingEnabled = true;
        MC.player.getAbilities().allowModifyWorld = canBreakBlocks;
        MC.gameRenderer.setRenderHand(true);
        MC.player.noClip = false;

        if (clone != null) {
            clone.despawn();
            clone = null;
        }

        MC.player.setVelocity(Vec3d.ZERO);
        MC.player.getAbilities().flying = isFlying;
        MC.player.getAbilities().setFlySpeed(flightSpeed);

        MC.player.updatePosition(pos.x, pos.y, pos.z);

        if (riding != null && MC.world.getEntityById(riding.getId()) != null) {
            MC.player.startRiding(riding);
            riding = null;
        }

        MC.player.setPose(pose);
        MC.player.setYaw(rot[0]);
        MC.player.setPitch(rot[1]);
        MC.player.setBodyYaw(rot[2]);
        MC.player.limbAngle = limbs[0];
        MC.player.limbDistance = limbs[1];

        if (isFallFlying) {
            MC.player.startFallFlying();
        }

        if (ModConfig.INSTANCE.notify) {
            MC.player.sendMessage(new LiteralText(ModConfig.INSTANCE.disableMessage), true);
        }
    }

    public static void updatePositionLook(PlayerPositionLookS2CPacket packet) {
        pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        rot[0] = packet.getYaw();
        rot[1] = packet.getPitch();

        clone.setPosition(pos);
        clone.setYaw(rot[0]);
        clone.setPitch(rot[1]);
    }

    public static void updateRiding(Entity entity, boolean force) {
        riding = entity;
        if (ModConfig.INSTANCE.showClone && clone != null) {
            clone.startRiding(entity, force);
        }
    }

    public static ClonePlayerEntity getClone() {
        return clone;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
