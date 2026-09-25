package net.xolt.freecam.config.model;

import net.minecraft.world.level.block.Block;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.network.ServerPolicies;

import java.util.ArrayList;
import java.util.List;

public class ModConfigImpl implements ModConfig {

    private final ModConfigDTO data;
    private final ServerPolicies serverPolicies;
    private final CollisionPredicate collisionPredicate;

    public ModConfigImpl(ModConfigDTO data, ServerPolicies serverPolicies) {
        this.data = data;
        this.serverPolicies = serverPolicies;
        collisionPredicate = CollisionPredicate.create(data.collision);
    }

    public ModConfigDTO getData() {
        return data;
    }

    @Override
    public FlightMode getFlightMode() {
        return data.movement.flightMode;
    }

    @Override
    public double getHorizontalSpeed() {
        return data.movement.horizontalSpeed;
    }

    @Override
    public double getVerticalSpeed() {
        return data.movement.verticalSpeed;
    }

    @Override
    public boolean ignoreAllCollision() {
        return serverPolicies.allowIgnoringCollision() && data.collision.ignoreAll;
    }

    @Override
    public boolean shouldCheckInitialCollision() {
        return !serverPolicies.allowIgnoringCollision() || data.collision.alwaysCheck || !data.collision.ignoreAll;
    }

    @Override
    public boolean ignoreCollisionWith(Block block) {
        return serverPolicies.allowIgnoringCollision() && (data.collision.ignoreAll || collisionPredicate.shouldIgnore(block));
    }

    @Override
    public Perspective getInitialPerspective() {
        return data.visual.perspective;
    }

    @Override
    public boolean shouldShowPlayer() {
        return data.visual.showPlayer;
    }

    @Override
    public boolean shouldShowHand() {
        return data.visual.showHand;
    }

    @Override
    public boolean isFullBrightEnabled() {
        return serverPolicies.allowFullbright() && data.visual.fullBright;
    }

    @Override
    public boolean shouldShowSubmersionFog() {
        return data.visual.showSubmersion;
    }

    @Override
    public boolean shouldDisableOnDamage() {
        return data.utility.disableOnDamage;
    }

    @Override
    public boolean shouldFreezePlayer() {
        return data.utility.freezePlayer;
    }

    @Override
    public boolean shouldPreventInteractions() {
        // Servers may only restrict interactions from the camera's perspective.
        boolean restrictedByServer = data.utility.interactionMode == ModConfigDTO.InteractionMode.CAMERA
                && !serverPolicies.allowCameraInteractions();
        return !data.utility.allowInteract || restrictedByServer;
    }

    public boolean allowInteractionsFrom(ModConfigDTO.InteractionMode mode) {
        return data.utility.interactionMode == mode && !shouldPreventInteractions();
    }

    @Override
    public List<ServerRestrictedFeature> getServerRestrictedFeatures() {
        List<ServerRestrictedFeature> restricted = new ArrayList<>();
        boolean ignoresCollision = data.collision.ignoreAll || data.collision.ignoreTransparent
                || data.collision.ignoreOpenable || data.collision.ignoreCustom;
        if (ignoresCollision && !serverPolicies.allowIgnoringCollision()) {
            restricted.add(ServerRestrictedFeature.IGNORING_COLLISION);
        }
        if (data.visual.fullBright && !serverPolicies.allowFullbright()) {
            restricted.add(ServerRestrictedFeature.FULL_BRIGHTNESS);
        }
        if (data.utility.allowInteract && data.utility.interactionMode == ModConfigDTO.InteractionMode.CAMERA
                && !serverPolicies.allowCameraInteractions()) {
            restricted.add(ServerRestrictedFeature.CAMERA_INTERACTIONS);
        }
        return restricted;
    }

    @Override
    public boolean allowInteractionsFromCamera() {
        return allowInteractionsFrom(ModConfigDTO.InteractionMode.CAMERA);
    }

    @Override
    public boolean allowInteractionsFromPlayer() {
        return allowInteractionsFrom(ModConfigDTO.InteractionMode.PLAYER);
    }

    @Override
    public boolean isRestrictedOnServer(String serverIp) {
        return switch (data.servers.mode) {
            case NONE -> false;
            case WHITELIST -> {
                String ip = serverIp.trim().toLowerCase();
                yield data.servers.whitelist.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .noneMatch(ip::equals);
            }
            case BLACKLIST -> {
                String ip = serverIp.trim().toLowerCase();
                yield data.servers.blacklist.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .anyMatch(ip::equals);
            }
        };
    }

    @Override
    public boolean shouldNotifyFreecam() {
        return data.notification.notifyFreecam;
    }

    @Override
    public boolean shouldNotifyTripod() {
        return data.notification.notifyTripod;
    }

    @Override
    public boolean shouldOutlinePlayer() {
        return data.visual.outlinePlayer;
    }
}
