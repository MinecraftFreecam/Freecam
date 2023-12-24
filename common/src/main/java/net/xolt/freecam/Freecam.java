package net.xolt.freecam;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

import static net.xolt.freecam.config.ModBindings.*;

public class Freecam {

    public static final Minecraft MC = Minecraft.getInstance();
    public static final String MOD_ID = "freecam";

    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static Integer activeTripod = null;
    private static FreeCamera freeCamera;
    private static HashMap<Integer, FreecamPosition> overworld_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> nether_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> end_tripods = new HashMap<>();
    private static CameraType rememberedF5 = null;

    public static void preTick(Minecraft mc) {
        if (isEnabled()) {
            // Disable if the previous tick asked us to
            if (disableNextTick()) {
                toggle();
                disableNextTick = false;
            }

            // Prevent player from being controlled when freecam is enabled
            if (mc.player != null && mc.player.input instanceof KeyboardInput && !isPlayerControlEnabled()) {
                Input input = new Input();
                input.shiftKeyDown = mc.player.input.shiftKeyDown; // Makes player continue to sneak after freecam is enabled.
                mc.player.input = input;
            }

            mc.gameRenderer.setRenderHand(ModConfig.INSTANCE.visual.showHand);
        }
    }

    public static void postTick(Minecraft mc) {
        if (KEY_TRIPOD_RESET.isPressed()) {
            for (KeyMapping hotbarKey : mc.options.keyHotbarSlots) {
                while (hotbarKey.consumeClick()) {
                    resetCamera(hotbarKey.getDefaultKey().getValue());
                    while (KEY_TRIPOD_RESET.wasPressed()) {}
                }
            }
        }

        if (KEY_TOGGLE.isPressed()) {
            for (KeyMapping hotbarKey : mc.options.keyHotbarSlots) {
                while (hotbarKey.consumeClick()) {
                    toggleTripod(hotbarKey.getDefaultKey().getValue());
                    while (KEY_TOGGLE.wasPressed()) {}
                }
            }
        } else if (KEY_TOGGLE.wasPressed()) {
            toggle();
            while (KEY_TOGGLE.wasPressed()) {}
        }

        while (KEY_PLAYER_CONTROL.wasPressed()) {
            switchControls();
        }

        while (KEY_CONFIG_GUI.wasPressed()) {
            mc.setScreen(AutoConfig.getConfigScreen(ModConfig.class, mc.screen).get());
        }
    }

    public static void onDisconnect() {
        if (isEnabled()) {
            toggle();
        }
        clearTripods();
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
        if (!freecamEnabled) {
            onDisabled();
        }
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
        if (!tripodEnabled) {
            onDisabled();
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
            chunkLoaded = MC.level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z);
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

        if (ModConfig.INSTANCE.notification.notifyTripod) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.openTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
        }
    }

    private static void onDisableTripod() {
        getTripodsForDimension().put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyTripod) {
                MC.player.displayClientMessage(Component.translatable("msg.freecam.closeTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
            }
        }
        activeTripod = null;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera(-420);
        freeCamera.applyPerspective(ModConfig.INSTANCE.visual.perspective, ModConfig.INSTANCE.collision.alwaysCheck || !ModConfig.INSTANCE.collision.ignoreAll);
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);

        if (ModConfig.INSTANCE.notification.notifyFreecam) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.enable"), true);
        }
    }

    private static void onDisableFreecam() {
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyFreecam) {
                MC.player.displayClientMessage(Component.translatable("msg.freecam.disable"), true);
            }
        }
    }

    private static void onEnable() {
        MC.smartCull = false;
        MC.gameRenderer.setRenderHand(ModConfig.INSTANCE.visual.showHand);

        rememberedF5 = MC.options.getCameraType();
        if (MC.gameRenderer.getMainCamera().isDetached()) {
            MC.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }

    private static void onDisable() {
        MC.smartCull = true;
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

    private static void onDisabled() {
        if (rememberedF5 != null) {
            MC.options.setCameraType(rememberedF5);
        }
    }

    private static void resetCamera(int keyCode) {
        if (tripodEnabled && activeTripod != null && activeTripod == keyCode && freeCamera != null) {
            freeCamera.copyPosition(MC.player);
        } else {
            getTripodsForDimension().put(keyCode, null);
        }

        if (ModConfig.INSTANCE.notification.notifyTripod) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
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
        switch (MC.level.dimensionTypeId().location().getPath()) {
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
