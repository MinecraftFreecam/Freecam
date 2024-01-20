package net.xolt.freecam;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.tripod.TripodRegistry;
import net.xolt.freecam.tripod.TripodSlot;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
import net.xolt.freecam.variant.api.BuildVariant;
import org.jetbrains.annotations.Nullable;

public class Freecam {

    public static final Minecraft MC = Minecraft.getInstance();
    public static final String MOD_ID = "freecam";

    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static final TripodRegistry tripods = new TripodRegistry();
    private static TripodSlot activeTripod = TripodSlot.NONE;
    private static FreeCamera freeCamera;
    private static CameraType rememberedF5 = null;

    public static void preTick(Minecraft mc) {
        // Disable if the previous tick asked us to
        if (disableNextTick && isEnabled()) {
            toggle();
        }
        disableNextTick = false;

        if (isEnabled()) {
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
        ModBindings.forEach(Tickable::tick);
    }

    public static void onDisconnect() {
        if (isEnabled()) {
            toggle();
        }
        tripods.clear();
    }

    public static boolean activateTripodHandler() {
        boolean activated = false;
        for (KeyMapping combo : MC.options.keyHotbarSlots) {
            while (combo.consumeClick()) {
                toggleTripod(TripodSlot.ofKeyCode(combo.getDefaultKey().getValue()));
                activated = true;
            }
        }
        return activated;
    }

    public static boolean resetTripodHandler() {
        boolean reset = false;
        for (KeyMapping key : MC.options.keyHotbarSlots) {
            while (key.consumeClick()) {
                resetCamera(TripodSlot.ofKeyCode(key.getDefaultKey().getValue()));
                reset = true;
            }
        }
        return reset;
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
        if (position == null) {
            moveToPlayer();
        } else {
            moveToPosition(position);
        }

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
        moveToPlayer();
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
            moveToPlayer();
        } else {
            tripods.put(tripod, null);
        }

        if (ModConfig.INSTANCE.notification.notifyTripod) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.tripodReset", tripod), true);
        }
    }

    public static void moveToEntity(@Nullable Entity entity) {
        if (freeCamera == null) {
            return;
        }
        if (entity == null) {
            moveToPlayer();
            return;
        }
        freeCamera.copyPosition(entity);
    }

    public static void moveToPosition(@Nullable FreecamPosition position) {
        if (freeCamera == null) {
            return;
        }
        if (position == null) {
            moveToPlayer();
            return;
        }
        freeCamera.applyPosition(position);
    }

    public static void moveToPlayer() {
        if (freeCamera == null) {
            return;
        }
        freeCamera.copyPosition(MC.player);
        freeCamera.applyPerspective(
                ModConfig.INSTANCE.visual.perspective,
                ModConfig.INSTANCE.collision.alwaysCheck || !(ModConfig.INSTANCE.collision.ignoreAll && BuildVariant.getInstance().cheatsPermitted())
        );
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
