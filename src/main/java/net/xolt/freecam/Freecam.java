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
    private static KeyBinding tripodResetBind;
    private static boolean enabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static Integer activeTripod = null;

    private static FreeCamera freeCamera;
    private static HashMap<Integer, FreeCamera> tripods = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        freecamBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "category.freecam.freecam"));
        playerControlBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.freecam.playerControl", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));
        tripodResetBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.tripodReset", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (tripodResetBind.isPressed()) {
                for (KeyBinding hotbarKey : MC.options.hotbarKeys) {
                    while (hotbarKey.wasPressed()) {
                        resetCamera(hotbarKey.getDefaultKey().getCode());
                        while (tripodResetBind.wasPressed()) {}
                    }
                }
            }

            if (freecamBind.isPressed()) {
                for (KeyBinding hotbarKey : MC.options.hotbarKeys) {
                    while (hotbarKey.wasPressed()) {
                        toggleTripod(hotbarKey.getDefaultKey().getCode());
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
        if (tripodEnabled) {
            toggleTripod(activeTripod);
        } else {
            if (enabled) {
                onDisableFreecam();
            } else {
                onEnableFreecam();
            }
            enabled = !enabled;
        }
    }

    public static void toggleTripod() {
        toggleTripod(activeTripod);
    }

    private static void toggleTripod(int keyCode) {
        if (tripodEnabled) {
            if (activeTripod.equals(keyCode)) {
                onDisableTripod(keyCode);
                tripodEnabled = false;
            } else {
                onDisable();
                tripods.get(activeTripod).input = new Input();
                onEnableTripod(keyCode);
            }
        } else {
            if (enabled) {
                toggle();
            }
            onEnableTripod(keyCode);
            tripodEnabled = true;
        }
    }

    private static void resetCamera(int keyCode) {
        FreeCamera camera = tripods.get(keyCode);
        if (camera != null) {
            camera.copyPositionAndRotation(MC.player);
            if (ModConfig.INSTANCE.notifyTripod) {
                MC.player.sendMessage(new TranslatableText("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
            }
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

    private static void onEnableTripod(int keyCode) {
        onEnable();
        FreeCamera tripod = tripods.get(keyCode);

        boolean chunkLoaded = false;
        if (tripod != null) {
            ChunkPos chunkPos = tripod.getChunkPos();
            chunkLoaded = MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z);
        }

        if (tripod == null) {
            tripod = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0));
            tripods.put(keyCode, tripod);
            tripod.spawn();
        } else if (!chunkLoaded) {
            resetCamera(keyCode);
        }

        tripod.input = new KeyboardInput(MC.options);
        MC.setCameraEntity(tripod);
        activeTripod = keyCode;

        if (ModConfig.INSTANCE.notifyTripod) {
            MC.player.sendMessage(new TranslatableText("msg.freecam.openTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
        }
    }

    private static void onDisableTripod(int keyCode) {
        onDisable();
        tripods.get(keyCode).input = new Input();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notifyTripod) {
                MC.player.sendMessage(new TranslatableText("msg.freecam.closeTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
            }
        }
        activeTripod = null;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera(-420);
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

    public static void clearTripods() {
        for (Integer key : tripods.keySet()) {
            tripods.get(key).despawn();
        }
        tripods = new HashMap<>();
    }

    public static FreeCamera getFreeCamera() {
        FreeCamera result = null;
        if (enabled) {
            result = freeCamera;
        } else if (tripodEnabled) {
            result = tripods.get(activeTripod);
        }
        return result;
    }

    public static KeyBinding getFreecamBind() {
        return freecamBind;
    }

    public static KeyBinding getTripodResetBind() {
        return tripodResetBind;
    }

    public static boolean isEnabled() {
        return enabled || tripodEnabled;
    }

    public static boolean isFreecamEnabled() {
        return enabled;
    }

    public static boolean isTripodEnabled() {
        return tripodEnabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }
}
