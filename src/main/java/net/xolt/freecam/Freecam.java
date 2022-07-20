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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.ChunkPos;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class Freecam implements ClientModInitializer {

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    private static KeyBinding freecamBind;
    private static KeyBinding playerControlBind;
    private static boolean enabled = false;
    private static boolean persistentCameraEnabled = false;
    private static boolean playerControlEnabled = false;
    private static Integer activePersistentCamera = null;

    private static FreeCamera freeCamera;
    private static HashMap<Integer, FreeCamera> persistentCameras = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        freecamBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "category.freecam.freecam"));
        playerControlBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.freecam.playerControl", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (freecamBind.isPressed()) {
                for (KeyBinding hotbarKey : MC.options.hotbarKeys) {
                    while (hotbarKey.wasPressed()) {
                        togglePersistentCamera(hotbarKey.getDefaultKey().getCode());
                        while (freecamBind.wasPressed()) {}
                    }
                }
            } else if (freecamBind.wasPressed()){
                toggle();
                while (freecamBind.wasPressed()) {}
            }

            while (playerControlBind.wasPressed()) {
                switchControls();
            }
        });
    }

    public static void toggle() {
        if (persistentCameraEnabled) {
            togglePersistentCamera(activePersistentCamera);
        } else {
            if (enabled) {
                onDisableFreecam();
            } else {
                onEnableFreecam();
            }
            enabled = !enabled;
        }
    }

    public static void togglePersistentCamera() {
        togglePersistentCamera(activePersistentCamera);
    }

    private static void togglePersistentCamera(int keyCode) {
        if (persistentCameraEnabled) {
            if (activePersistentCamera.equals(keyCode)) {
                onDisablePersistentCamera(keyCode);
                persistentCameraEnabled = false;
            } else {
                onDisable();
                persistentCameras.get(activePersistentCamera).input = new Input();
                onEnablePersistentCamera(keyCode);
            }
        } else {
            if (enabled) {
                toggle();
            }
            onEnablePersistentCamera(keyCode);
            persistentCameraEnabled = true;
        }
    }

    public static void switchControls() {
        if (isEnabled()) {
            if (playerControlEnabled) {
                getFreeCamera().input = new KeyboardInput(MC.options);
            } else {
                MC.player.input = new KeyboardInput(MC.options);
                getFreeCamera().input = new Input();
            }
            playerControlEnabled = !playerControlEnabled;
        }
    }

    private static void onEnablePersistentCamera(int keyCode) {
        onEnable();
        FreeCamera persistentCamera = persistentCameras.get(keyCode);

        boolean chunkLoaded = false;
        if (persistentCamera != null) {
            ChunkPos chunkPos = persistentCamera.getChunkPos();
            chunkLoaded = MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z);
        }

        if (persistentCamera == null || !chunkLoaded) {
            persistentCamera = new FreeCamera();
            persistentCameras.put(keyCode, persistentCamera);
            persistentCamera.spawn();
        }

        persistentCamera.input = new KeyboardInput(MC.options);
        MC.setCameraEntity(persistentCamera);
        activePersistentCamera = keyCode;

        if (ModConfig.INSTANCE.notifyPersistent) {
            MC.player.sendMessage(new TranslatableText("msg.freecam.enablePersistent").append("" + activePersistentCamera % GLFW.GLFW_KEY_0), true);
        }
    }

    private static void onDisablePersistentCamera(int keyCode) {
        onDisable();
        persistentCameras.get(keyCode).input = new Input();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notifyPersistent) {
                MC.player.sendMessage(new TranslatableText("msg.freecam.disablePersistent").append("" + activePersistentCamera % GLFW.GLFW_KEY_0), true);
            }
        }
        activePersistentCamera = null;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera();
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);

        if (ModConfig.INSTANCE.notifyFreecam) {
            MC.player.sendMessage(new TranslatableText("msg.freecam.enable"), true);
        }
    }

    private static void onDisableFreecam() {
        onDisable();
        freeCamera.despawn();
        freeCamera = null;

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notifyFreecam) {
                MC.player.sendMessage(new TranslatableText("msg.freecam.disable"), true);
            }
        }
    }

    private static void onEnable() {
        MC.chunkCullingEnabled = false;
        MC.gameRenderer.setRenderHand(ModConfig.INSTANCE.showHand);

        if (MC.gameRenderer.getCamera().isThirdPerson()) {
            MC.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    private static void onDisable() {
        MC.chunkCullingEnabled = true;
        MC.gameRenderer.setRenderHand(true);
        MC.setCameraEntity(MC.player);
        playerControlEnabled = false;

        if (MC.player != null) {
            MC.player.input = new KeyboardInput(MC.options);
        }
    }

    public static void clearPersistentCameras() {
        persistentCameras = new HashMap<>();
    }

    public static FreeCamera getFreeCamera() {
        FreeCamera result = null;
        if (enabled) {
            result = freeCamera;
        } else if (persistentCameraEnabled) {
            result = persistentCameras.get(activePersistentCamera);
        }
        return result;
    }

    public static KeyBinding getFreecamBind() {
        return freecamBind;
    }

    public static boolean isEnabled() {
        return enabled || persistentCameraEnabled;
    }

    public static boolean isFreecamEnabled() {
        return enabled;
    }

    public static boolean isPersistentCameraEnabled() {
        return persistentCameraEnabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }
}
