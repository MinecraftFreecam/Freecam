package net.xolt.freecam.config.model;

import net.minecraft.world.level.block.Block;
import net.xolt.freecam.config.MCAwareModConfig;

public class ModConfigDTOAdapter implements MCAwareModConfig {

    private final ModConfigDTO data;
    private final CollisionPredicate collisionPredicate;

    public ModConfigDTOAdapter(ModConfigDTO data) {
        this.data = data;
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
        return data.collision.ignoreAll;
    }

    @Override
    public boolean shouldCheckInitialCollision() {
        return data.collision.alwaysCheck || !data.collision.ignoreAll;
    }

    @Override
    public boolean ignoreCollisionWith(Block block) {
        return data.collision.ignoreAll || collisionPredicate.shouldIgnore(block);
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
        return data.visual.fullBright;
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
        return !data.utility.allowInteract;
    }

    public boolean allowInteractionsFrom(ModConfigDTO.InteractionMode mode) {
        return data.utility.allowInteract && data.utility.interactionMode == mode;
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
