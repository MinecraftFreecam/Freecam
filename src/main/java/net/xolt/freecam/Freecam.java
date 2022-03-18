package net.xolt.freecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.lwjgl.glfw.GLFW;

public class Freecam implements ClientModInitializer {

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    private static KeyBinding keyBinding;
    private static boolean enabled = false;

    private static FreeCamera freeCamera;

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
            onDisable();
        } else {
            onEnable();
        }
        enabled = !enabled;
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
            MC.player.sendMessage(new TranslatableText("msg.freecam.enable"), true);
        }
    }

    private static void onDisable() {
        MC.chunkCullingEnabled = true;
        MC.gameRenderer.setRenderHand(true);
        MC.player.input = new KeyboardInput(MC.options);
        MC.setCameraEntity(MC.player);
        freeCamera.despawn();
        freeCamera = null;

        if (ModConfig.INSTANCE.notify) {
            MC.player.sendMessage(new TranslatableText("msg.freecam.disable"), true);
        }
    }

    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
