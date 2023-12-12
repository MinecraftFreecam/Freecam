package net.xolt.freecam.config;

import net.minecraft.entity.Entity;
import net.xolt.freecam.BuildConfig;
import net.xolt.freecam.Freecam;

import static net.xolt.freecam.Freecam.MC;
import static net.xolt.freecam.config.ModConfig.InteractionMode.PLAYER;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Behaviour {

    public static final ModConfig CONFIG = ModConfig.INSTANCE;

    private Behaviour() {}

    @SuppressWarnings("SimplifiableConditionalExpression")
    public static boolean allowCheats() {
        return BuildConfig.CHEATS_RESTRICTED
                ? MC.player.hasPermissionLevel(2) || MC.player.isCreative() || MC.isInSingleplayer()
                : true;
    }

    public static boolean checkInitialCollision() {
        return ModConfig.INSTANCE.collision.alwaysCheck || !(ModConfig.INSTANCE.collision.ignoreAll && allowCheats());
    }

    public static boolean allowFreezePlayer() {
        return CONFIG.utility.freezePlayer && allowCheats() && !Freecam.isPlayerControlEnabled();
    }

    public static boolean allowInteract() {
        return CONFIG.utility.allowInteract && (allowCheats() || CONFIG.utility.interactionMode.equals(PLAYER));
    }

    public static boolean disableInteract() {
        return Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !allowInteract();
    }

    public static boolean freezePlayer(Entity entity) {
        return Freecam.isEnabled() && entity.equals(MC.player) && allowFreezePlayer();
    }
}
