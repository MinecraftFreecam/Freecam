package net.xolt.freecam.config;

import net.minecraft.world.level.block.Block;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.controller.BasicConfigController;
import net.xolt.freecam.config.controller.ConfigController;
import net.xolt.freecam.config.controller.ConfigControllerRegistry;
import net.xolt.freecam.config.controller.ModConfigController;
import net.xolt.freecam.config.load.ConfigLoader;
import net.xolt.freecam.config.load.ConfigSerializer;
import net.xolt.freecam.config.load.ModConfigLoader;
import net.xolt.freecam.config.load.RawJsonPreservingSerializer;
import net.xolt.freecam.config.model.FlightMode;
import net.xolt.freecam.config.model.ModConfigDTO;
import net.xolt.freecam.config.model.ModConfigImpl;
import net.xolt.freecam.config.model.Perspective;

import java.nio.file.Path;

import static net.xolt.freecam.Freecam.MC;

public interface ModConfig {

    /**
     * Called once, early during mod initialization.
     * Will load config from disk and perform internal setup.
     */
    static void setup() {
        Path configDir = MC.gameDirectory.toPath().resolve("config");
        ConfigSerializer<?> serializer = new RawJsonPreservingSerializer();
        ConfigLoader<ModConfigDTO> loader = new ModConfigLoader(serializer, Freecam.MOD_ID, configDir);

        ConfigController<ModConfigDTO> dtoController = new BasicConfigController<>(loader, ModConfigDTO::new);
        ConfigControllerRegistry.register(ModConfigDTO.class, dtoController);

        ConfigController<ModConfigImpl> controller = new ModConfigController(dtoController);
        ConfigControllerRegistry.register(ModConfigImpl.class, controller);

        controller.load();
    }

    static ModConfig get() {
        return ConfigControllerRegistry.get(ModConfigImpl.class).getConfig();
    }

    FlightMode getFlightMode();

    double getHorizontalSpeed();

    double getVerticalSpeed();

    boolean ignoreAllCollision();

    boolean shouldCheckInitialCollision();

    boolean ignoreCollisionWith(Block block);

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
