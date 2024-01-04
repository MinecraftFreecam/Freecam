package net.xolt.freecam;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.tripod.TripodRegistry;
import net.xolt.freecam.tripod.TripodSlot;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;

import static net.xolt.freecam.config.ModBindings.*;

public class Freecam {

    public static final Minecraft MC = Minecraft.getInstance();
    public static final String MOD_ID = "freecam";
    private static final long TOGGLE_KEY_MAX_TICKS = 10;

    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static boolean toggleKeyUsedWhileHeld = false;
    private static long toggleKeyHeldTicks = 0;
    private static final TripodRegistry tripods = new TripodRegistry();
    private static TripodSlot activeTripod = TripodSlot.NONE;
    private static FreeCamera freeCamera;
    private static CameraType rememberedF5 = null;

    public static void preTick(Minecraft mc) {
        if (isEnabled()) {
            // Disable if the previous tick asked us to
            if (disableNextTick) {
                toggle();
            }

            // Prevent player from being controlled when freecam is enabled
            if (mc.player != null && mc.player.input instanceof KeyboardInput && !isPlayerControlEnabled()) {
                Input input = new Input();
                input.shiftKeyDown = mc.player.input.shiftKeyDown; // Makes player continue to sneak after freecam is enabled.
                mc.player.input = input;
            }

            mc.gameRenderer.setRenderHand(ModConfig.get().visual.showHand);
        }
        disableNextTick = false;
    }

    public static void postTick(Minecraft mc) {
        if (KEY_TOGGLE.isDown()) {
            // Count held ticks, so we can toggle on release
            toggleKeyHeldTicks++;
            KEY_TOGGLE.reset();

            // Handle <toggle_key>+<hotbar_key> combos
            for (KeyMapping combo : mc.options.keyHotbarSlots) {
                while (combo.consumeClick()) {
                    toggleTripod(TripodSlot.ofKeyCode(combo.getDefaultKey().getValue()));
                    toggleKeyUsedWhileHeld = true;
                }
            }
        }
        // Check if toggle was pressed, and is now released
        else if (KEY_TOGGLE.consumeClick() || toggleKeyHeldTicks > 0) {
            // Only toggle if the key wasn't used (or held too long)
            if (!toggleKeyUsedWhileHeld && toggleKeyHeldTicks < TOGGLE_KEY_MAX_TICKS) {
                toggle();
            }
            // Reset state
            KEY_TOGGLE.reset();
            toggleKeyHeldTicks = 0;
            toggleKeyUsedWhileHeld = false;
        }

        // Handle <reset_key>+<hotbar_key> combos
        if (KEY_TRIPOD_RESET.isDown()) {
            for (KeyMapping key : mc.options.keyHotbarSlots) {
                while (key.consumeClick()) {
                    resetCamera(TripodSlot.ofKeyCode(key.getDefaultKey().getValue()));
                }
            }
        }

        while (KEY_PLAYER_CONTROL.consumeClick()) {
            switchControls();
        }

        while (KEY_CONFIG_GUI.consumeClick()) {
            mc.setScreen(ModConfig.getScreen(mc.screen));
        }
    }

    public static void onDisconnect() {
        if (isEnabled()) {
            toggle();
        }
        tripods.clear();
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

    public static void gotoPosition(FreecamPosition position) {
        boolean wasEnabled = isEnabled();
        if (!wasEnabled) {
            // FIXME this will move the camera to the default position and show a notification,
            //  even though we'll move the camera and show our own notification immediately after.
            toggle();
        }

        freeCamera.applyPosition(position);

        if (ModConfig.get().notification.notifyGoto) {
            Component notification;
            if (!wasEnabled) {
                notification = Component.translatable("msg.freecam.gotoPosition.enable", position.getName());
            } else if (tripodEnabled) {
                notification = Component.translatable("msg.freecam.gotoPosition.moveTripod", activeTripod, position.getName());
            } else {
                notification = Component.translatable("msg.freecam.gotoPosition.move", position.getName());
            }
            MC.player.displayClientMessage(notification, true);
        }
    }

    private static void toggleTripod(TripodSlot tripod) {
        if (tripod == TripodSlot.NONE) {
            return;
        }

        if (tripodEnabled) {
            if (activeTripod == tripod) {
                onDisableTripod();
                tripodEnabled = false;
            } else {
                onDisableTripod();
                onEnableTripod(tripod);
            }
        } else {
            if (freecamEnabled) {
                toggle();
            }
            onEnableTripod(tripod);
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

    private static void onEnableTripod(TripodSlot tripod) {
        onEnable();

        FreecamPosition position = FreecamPosition.of(tripod);
        while (!position.isInRange()) {
            resetCamera(tripod);
            position = FreecamPosition.of(tripod);
        }

        freeCamera = new FreeCamera(-420 - tripod.ordinal());
        freeCamera.applyPosition(position);

        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);
        activeTripod = tripod;

        if (ModConfig.get().notification.notifyTripod) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.openTripod", tripod), true);
        }
    }

    private static void onDisableTripod() {
        tripods.put(activeTripod, FreecamPosition.of(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.get().notification.notifyTripod) {
                MC.player.displayClientMessage(Component.translatable("msg.freecam.closeTripod", activeTripod), true);
            }
        }
        activeTripod = TripodSlot.NONE;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera(-420);
        freeCamera.applyPosition(FreecamPosition.defaultPosition());
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);

        if (ModConfig.get().notification.notifyFreecam) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.enable"), true);
        }
    }

    private static void onDisableFreecam() {
        onDisable();

        if (MC.player != null) {
            if (ModConfig.get().notification.notifyFreecam) {
                MC.player.displayClientMessage(Component.translatable("msg.freecam.disable"), true);
            }
        }
    }

    private static void onEnable() {
        MC.smartCull = false;
        MC.gameRenderer.setRenderHand(ModConfig.get().visual.showHand);

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

    private static void resetCamera(TripodSlot tripod) {
        if (tripodEnabled && activeTripod != TripodSlot.NONE && activeTripod == tripod && freeCamera != null) {
            freeCamera.applyPosition(FreecamPosition.defaultPosition());
        } else {
            tripods.put(tripod, null);
        }

        if (ModConfig.get().notification.notifyTripod) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.tripodReset", tripod), true);
        }
    }

    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    public static void disableNextTick() {
        disableNextTick = true;
    }

    public static boolean isEnabled() {
        return freecamEnabled || tripodEnabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }

    public static FreecamPosition getTripod(TripodSlot tripod) {
        return tripods.get(tripod);
    }
}
