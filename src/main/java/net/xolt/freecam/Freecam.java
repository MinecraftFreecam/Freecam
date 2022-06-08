package net.xolt.freecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.lwjgl.glfw.GLFW;

public class Freecam implements ClientModInitializer {

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    private static KeyBinding freecamBind;
    private static KeyBinding playerControlBind;
    private static boolean enabled = false;
    private static boolean playerControlEnabled = false;

    private static FreeCamera freeCamera;

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        freecamBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.freecam.freecam"));
        playerControlBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.freecam.playerControl", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (freecamBind.wasPressed()) {
                toggle();
            }
            while (playerControlBind.wasPressed()) {
                switchControls();
            }
        });
    }

    public static void toggle() {
        if (enabled) {
            onDisable();
        } else {
            onEnable();
        }
        enabled = !enabled;
    }

    public static void switchControls() {
        if (enabled) {
            if (playerControlEnabled) {
                freeCamera.input = new KeyboardInput(MC.options);
            } else {
                MC.player.input = new KeyboardInput(MC.options);
                freeCamera.input = new Input();
            }
            playerControlEnabled = !playerControlEnabled;
        }
    }

    private static void onEnable() {
        MC.chunkCullingEnabled = false;
        MC.gameRenderer.setRenderHand(ModConfig.INSTANCE.showHand);
        freeCamera = new FreeCamera();
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);

        if (MC.gameRenderer.getCamera().isThirdPerson()) {
            MC.options.setPerspective(Perspective.FIRST_PERSON);
        }

        if (ModConfig.INSTANCE.notify) {
            MC.player.sendMessage(Text.translatable("msg.freecam.enable"), true);
        }
    }

    private static void onDisable() {
        MC.chunkCullingEnabled = true;
        MC.gameRenderer.setRenderHand(true);
        MC.setCameraEntity(MC.player);
        freeCamera.despawn();
        freeCamera = null;
        playerControlEnabled = false;

        if (MC.player != null) {
            MC.player.input = new KeyboardInput(MC.options);
            if (ModConfig.INSTANCE.notify) {
                MC.player.sendMessage(Text.translatable("msg.freecam.disable"), true);
            }
        }
    }

    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }
}
