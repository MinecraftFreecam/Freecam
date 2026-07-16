package net.xolt.freecam.config;

import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.controller.ConfigControllerRegistry;
import net.xolt.freecam.config.controller.CoreConfigController;
import net.xolt.freecam.config.controller.ModConfigController;
import net.xolt.freecam.config.model.*;

import static net.xolt.freecam.Freecam.MC;

public interface ModConfig {

    /**
     * Called once, early during mod initialization.
     * Will load config from disk and perform internal setup.
     */
    static void setup() {
        CoreConfigLoader<ModConfigDTO> loader = new CoreConfigLoader<>(
            new GsonSerializer(),
            ModConfigDTO.class,
            MC.gameDirectory.toPath().resolve("config"),
            Freecam.MOD_ID
        );

        // Create a pure-data controller
        CoreConfigController<ModConfigDTO> dtoController = new CoreConfigController<>(loader, ModConfigDTO::new);
        ConfigControllerRegistry.register(ModConfigDTO.class, dtoController);

        // And an MC-aware wrapper
        ModConfigController adapterController = new ModConfigController(dtoController);
        ConfigControllerRegistry.register(ModConfigDTOAdapter.class, adapterController);

        // Load the config
        adapterController.load();
    }

    static ModConfig get() {
        return ConfigControllerRegistry.get(ModConfigDTOAdapter.class).getConfig();
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

    boolean shouldOutlinePlayer();
}
