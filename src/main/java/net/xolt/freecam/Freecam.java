package net.xolt.freecam;

import me.shedaniel.autoconfig.AutoConfig;
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
import net.minecraft.util.math.ChunkPos;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class Freecam implements ClientModInitializer {

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    private static KeyBinding freecamBind;
    private static KeyBinding playerControlBind;
    private static KeyBinding tripodResetBind;
    private static KeyBinding configGuiBind;
    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static Integer activeTripod = null;
    private static FreeCamera freeCamera;
    private static HashMap<Integer, FreecamPosition> overworld_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> nether_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> end_tripods = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        freecamBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "category.freecam.freecam"));
        playerControlBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.playerControl", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));
        tripodResetBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.tripodReset", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));
        configGuiBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.configGui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam"));

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
            } else if (freecamBind.wasPressed()) {
                toggle();
                while (freecamBind.wasPressed()) {}
            }

            while (playerControlBind.wasPressed()) {
                switchControls();
            }

            while (configGuiBind.wasPressed()) {
                MC.setScreen(AutoConfig.getConfigScreen(ModConfig.class, MC.currentScreen).get());
            }
        });
    }

    public static void toggle() {
        if (tripodEnabled) {
            toggleTripod(activeTripod);
            return;
        }

        if (freecamEnabled) {
            onDisableFreecam();
        } else {
            onEnableFreecam();
        }
        freecamEnabled = !freecamEnabled;
    }

    private static void toggleTripod(Integer keyCode) {
        if (keyCode == null) {
            return;
        }

        if (tripodEnabled) {
            if (activeTripod.equals(keyCode)) {
                onDisableTripod();
                tripodEnabled = false;
            } else {
                onDisableTripod();
                onEnableTripod(keyCode);
            }
        } else {
            if (freecamEnabled) {
                toggle();
            }
            onEnableTripod(keyCode);
            tripodEnabled = true;
        }
    }

    public static void switchControls() {
        if (!isEnabled()) {
            return;
        }

        if (playerControlEnabled) {
            freeCamera.input = new KeyboardInput(MC.options);
        } else {
            MC.player.input = new KeyboardInput(MC.options);
            freeCamera.input = new Input();
        }
        playerControlEnabled = !playerControlEnabled;
    }

    private static void onEnableTripod(int keyCode) {
        onEnable();

        FreecamPosition position = getTripodsForDimension().get(keyCode);
        boolean chunkLoaded = false;
        if (position != null) {
            ChunkPos chunkPos = position.getChunkPos();
            chunkLoaded = MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z);
        }

        if (!chunkLoaded) {
            resetCamera(keyCode);
            position = null;
        }

        if (position == null) {
            freeCamera = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0));
        } else {
            freeCamera = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0), position);
        }

        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);
        activeTripod = keyCode;

        if (ModConfig.INSTANCE.notifyTripod) {
            MC.player.sendMessage(Text.translatable("msg.freecam.openTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
        }
    }

    private static void onDisableTripod() {
        getTripodsForDimension().put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notifyTripod) {
                MC.player.sendMessage(Text.translatable("msg.freecam.closeTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
            }
        }
        activeTripod = null;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera(-420);
        freeCamera.applyPerspective(ModConfig.INSTANCE.perspective, ModConfig.INSTANCE.collision.alwaysCheck || !ModConfig.INSTANCE.collision.ignoreAll);
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);

        if (ModConfig.INSTANCE.notifyFreecam) {
            MC.player.sendMessage(Text.translatable("msg.freecam.enable"), true);
        }
    }

    private static void onDisableFreecam() {
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notifyFreecam) {
                MC.player.sendMessage(Text.translatable("msg.freecam.disable"), true);
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
        freeCamera.despawn();
        freeCamera.input = new Input();
        freeCamera = null;

        if (MC.player != null) {
            MC.player.input = new KeyboardInput(MC.options);
        }
    }

    private static void resetCamera(int keyCode) {
        if (tripodEnabled && activeTripod != null && activeTripod == keyCode && freeCamera != null) {
            freeCamera.copyPositionAndRotation(MC.player);
        } else {
            getTripodsForDimension().put(keyCode, null);
        }

        if (ModConfig.INSTANCE.notifyTripod) {
            MC.player.sendMessage(Text.translatable("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
        }
    }

    public static void clearTripods() {
        overworld_tripods = new HashMap<>();
        nether_tripods = new HashMap<>();
        end_tripods = new HashMap<>();
    }

    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    public static HashMap<Integer, FreecamPosition> getTripodsForDimension() {
        HashMap<Integer, FreecamPosition> result;
        switch (MC.world.getDimensionKey().getValue().getPath()) {
            case "the_nether":
                result = nether_tripods;
                break;
            case "the_end":
                result = end_tripods;
                break;
            default:
                result = overworld_tripods;
                break;
        }
        return result;
    }

    public static KeyBinding getFreecamBind() {
        return freecamBind;
    }

    public static KeyBinding getTripodResetBind() {
        return tripodResetBind;
    }

    public static boolean disableNextTick() {
        return disableNextTick;
    }

    public static void setDisableNextTick(boolean damage) {
        disableNextTick = damage;
    }

    public static boolean isEnabled() {
        return freecamEnabled || tripodEnabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }
}
