package net.xolt.freecam;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.tripod.TripodRegistry;
import net.xolt.freecam.tripod.TripodSlot;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
import net.xolt.freecam.variant.api.BuildVariant;
import org.jetbrains.annotations.ApiStatus;
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

    @ApiStatus.Internal
    public static void preTick(Minecraft mc) {
        // Disable if the previous tick asked us to,
        // or Freecam is restricted on the current server
        if ((disableNextTick || isRestrictedOnServer()) && isEnabled()) {
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

            mc.gameRenderer.renderHand = ModConfig.INSTANCE.visual.showHand;
        }
    }

    @ApiStatus.Internal
    public static void postTick(Minecraft mc) {
        ModBindings.forEach(Tickable::tick);
    }

    @ApiStatus.Internal
    public static void onDisconnect() {
        if (isEnabled()) {
            toggle();
        }
        tripods.clear();
    }

    @ApiStatus.Internal
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

    @ApiStatus.Internal
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

    @ApiStatus.AvailableSince("0.3.1")
    public static void toggle() {
        if (isRestrictedOnServer()) {
            if (ModConfig.INSTANCE.notification.notifyFreecam) {
                MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.restrictedByConfig", MC.getCurrentServer().ip), true);
            }
            return;
        }

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

        if (isRestrictedOnServer()) {
            if (ModConfig.INSTANCE.notification.notifyTripod) {
                MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.restrictedByConfig", MC.getCurrentServer().ip), true);
            }
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

    @ApiStatus.AvailableSince("1.1.8")
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
            MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.openTripod", tripod), true);
        }
    }

    private static void onDisableTripod() {
        tripods.put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyTripod) {
                MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.closeTripod", activeTripod), true);
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
            MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.enable"), true);
        }
    }

    private static void onDisableFreecam() {
        onDisable();

        if (MC.player != null) {
            if (ModConfig.INSTANCE.notification.notifyFreecam) {
                MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.disable"), true);
            }
        }
    }

    private static void onEnable() {
        MC.smartCull = false;
        MC.gameRenderer.renderHand = ModConfig.INSTANCE.visual.showHand;

        rememberedF5 = MC.options.getCameraType();
        if (MC.gameRenderer.getMainCamera().isDetached()) {
            MC.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }

    private static void onDisable() {
        MC.smartCull = true;
        MC.gameRenderer.renderHand = true;
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
            MC.player.displayClientMessage(new TranslatableComponent("msg.freecam.tripodReset", tripod), true);
        }
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
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

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
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

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
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

    @ApiStatus.AvailableSince("0.4.0")
    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    @ApiStatus.AvailableSince("1.2.3")
    public static void disableNextTick() {
        disableNextTick = true;
    }

    @ApiStatus.AvailableSince("0.2.2")
    public static boolean isEnabled() {
        return freecamEnabled || tripodEnabled;
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.0.0")
    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.4")
    public static boolean isRestrictedOnServer() {
        ServerData server = MC.getCurrentServer();
        ModConfig.ServerRestriction mode = ModConfig.INSTANCE.servers.mode;
        if (mode == ModConfig.ServerRestriction.NONE || server == null || MC.hasSingleplayerServer()) {
            return false;
        }

        String ip = server.ip.trim().toLowerCase();
        switch (mode) {
            case WHITELIST:
                return ModConfig.INSTANCE.servers.whitelist.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .noneMatch(ip::equals);
            case BLACKLIST:
                return ModConfig.INSTANCE.servers.blacklist.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .anyMatch(ip::equals);
            default:
                throw new IllegalStateException("Unexpected mode value in Freecam.isRestrictedOnServer: " + mode);
        }
    }
}
