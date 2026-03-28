package net.xolt.freecam.config;

import net.xolt.freecam.config.model.FlightMode;
import net.xolt.freecam.config.model.Perspective;

public interface ModConfig {

    /**
     * Called once, early during mod initialization.
     * Will load config from disk and perform internal setup.
     */
    static void setup() {
        ModConfigProvider.instance().setupConfig();
    }

    static ModConfig get() {
        return ModConfigProvider.instance().getConfig();
    }

    FlightMode getFlightMode();

    double getHorizontalSpeed();

    double getVerticalSpeed();

    boolean ignoreAllCollision();

    boolean shouldCheckInitialCollision();

    Perspective getInitialPerspective();

    boolean shouldShowPlayer();

    default boolean shouldHidePlayer() {
        return !shouldShowPlayer();
    }

    boolean shouldShowHand();

    default boolean shouldHideHand() {
        return !shouldShowHand();
    }

    boolean isFullBrightEnabled();

    boolean shouldShowSubmersionFog();

    default boolean shouldHideSubmersionFog() {
        return !shouldShowSubmersionFog();
    }

    boolean shouldDisableOnDamage();

    boolean shouldFreezePlayer();

    boolean shouldPreventInteractions();

    boolean allowInteractionsFromCamera();

    boolean allowInteractionsFromPlayer();

    boolean isRestrictedOnServer(String serverIp);

    boolean shouldNotifyFreecam();

    boolean shouldNotifyTripod();
}
