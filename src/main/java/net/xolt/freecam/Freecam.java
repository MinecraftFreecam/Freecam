package net.xolt.freecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.ClonePlayerEntity;
import org.lwjgl.glfw.GLFW;

public class Freecam implements ClientModInitializer {

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    private static KeyBinding keyBinding;
    private static boolean enabled = false;

    private static Vec3d pos;
    private static float[] rot;
    private static Entity riding;
    private static boolean isFallFlying;
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

    private static void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    private static void onEnable() {
        MC.chunkCullingEnabled = false;
        pos = MC.player.getPos();
        rot = new float[]{MC.player.getYaw(), MC.player.getPitch()};
        isFallFlying = MC.player.isFallFlying();

        if (!ModConfig.INSTANCE.showHand) {
            MC.gameRenderer.setRenderHand(false);
        }

        if (MC.player.getVehicle() != null) {
            riding = MC.player.getVehicle();
            MC.player.getVehicle().removeAllPassengers();
        }

        if (ModConfig.INSTANCE.showClone) {
            clone = new ClonePlayerEntity(MC.world, MC.player);
            MC.world.addEntity(clone.getId(), clone);
            if (riding != null) {
                clone.startRiding(riding);
            }
        }

        if (isFallFlying) {
            MC.player.stopFallFlying();
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
        MC.gameRenderer.setRenderHand(true);
        MC.player.noClip = false;
        MC.player.setVelocity(Vec3d.ZERO);
        if (isFallFlying) {
            MC.player.startFallFlying();
        }

        if (clone != null) {
            MC.world.removeEntity(clone.getId(), Entity.RemovalReason.DISCARDED);
        }

        MC.player.updatePosition(pos.x, pos.y, pos.z);
        MC.player.setYaw(rot[0]);
        MC.player.setPitch(rot[1]);

        if (riding != null && MC.world.getEntityById(riding.getId()) != null) {
            MC.player.startRiding(riding);
            riding = null;
        }

        if (ModConfig.INSTANCE.notify) {
            MC.player.sendMessage(new LiteralText(ModConfig.INSTANCE.disableMessage), true);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
