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
import net.xolt.freecam.tripod.TripodRegistry;
import net.xolt.freecam.tripod.TripodSlot;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
import net.xolt.freecam.variant.api.BuildVariant;

import java.util.Objects;

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

            mc.gameRenderer.setRenderHand(ModConfig.INSTANCE.visual.showHand);
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
            mc.setScreen(AutoConfig.getConfigScreen(ModConfig.class, mc.screen).get());
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

        FreecamPosition position = tripods.get(tripod);
        boolean chunkLoaded = false;
        if (position != null) {
            ChunkPos chunkPos = position.getChunkPos();
            chunkLoaded = MC.level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z);
        }

        if (!chunkLoaded) {
            resetCamera(tripod);
            position = null;
        }

        freeCamera = new FreeCamera(-420 - tripod.ordinal());
        freeCamera.applyPosition(Objects.requireNonNullElseGet(position, () -> new FreecamPosition(MC.player)));
        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);
        activeTripod = tripod;

        if (ModConfig.INSTANCE.notification.notifyTripod) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.openTripod", tripod), true);
        }
    }

    private static void onDisableTripod() {
        tripods.put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyTripod) {
                MC.player.displayClientMessage(Component.translatable("msg.freecam.closeTripod", activeTripod), true);
            }
        }
        activeTripod = TripodSlot.NONE;
    }

    private static void onEnableFreecam() {
        onEnable();
        freeCamera = new FreeCamera(-420);
        freeCamera.applyPosition(new FreecamPosition(MC.player));
        freeCamera.applyPerspective(
                ModConfig.INSTANCE.visual.perspective,
                ModConfig.INSTANCE.collision.alwaysCheck || !(ModConfig.INSTANCE.collision.ignoreAll && BuildVariant.getInstance().cheatsPermitted())
        );
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

    private static void resetCamera(TripodSlot tripod) {
        if (tripodEnabled && activeTripod != TripodSlot.NONE && activeTripod == tripod && freeCamera != null) {
            freeCamera.copyPosition(MC.player);
        } else {
            tripods.put(tripod, null);
        }

        if (ModConfig.INSTANCE.notification.notifyTripod) {
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
}
