package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

@Config(name = "freecam")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public FlightMode flightMode = FlightMode.DEFAULT;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public InteractionMode interactionMode = InteractionMode.CAMERA;

    @ConfigEntry.Gui.Tooltip
    public double horizontalSpeed = 1.0;

    @ConfigEntry.Gui.Tooltip
    public double verticalSpeed = 1.0;

    @ConfigEntry.Gui.Tooltip
    public boolean noClip = true;

    @ConfigEntry.Gui.Tooltip
    public boolean disableOnDamage = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean allowInteract = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean freezePlayer = false;

    @ConfigEntry.Gui.Tooltip
    public boolean showPlayer = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showHand = false;

    @ConfigEntry.Gui.Tooltip
    public boolean showSubmersion = false;

    @ConfigEntry.Gui.Tooltip
    public boolean notifyFreecam = true;

    @ConfigEntry.Gui.Tooltip
    public boolean notifyTripod = true;

    public enum FlightMode implements SelectionListEntry.Translatable {
        CREATIVE("text.autoconfig.freecam.option.flightMode.creative"),
        DEFAULT("text.autoconfig.freecam.option.flightMode.default");

        private final String name;

        FlightMode(String name) {
            this.name = name;
        }

        public String getKey() {
            return name;
        }
    }

    public enum InteractionMode implements SelectionListEntry.Translatable {
        CAMERA("text.autoconfig.freecam.option.interactionMode.camera"),
        PLAYER("text.autoconfig.freecam.option.interactionMode.player");

        private final String name;

        InteractionMode(String name) {
            this.name = name;
        }

        public String getKey() {
            return name;
        }
    }
}
