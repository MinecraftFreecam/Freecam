package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

@Config(name = "freecam")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Comment("The type of flight used by freecam.")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public FlightMode flightMode = FlightMode.DEFAULT;

    @Comment("The horizontal speed of freecam.")
    public double horizontalSpeed = 1.0;

    @Comment("The vertical speed of freecam.")
    public double verticalSpeed = 1.0;

    @Comment("Whether you can travel through blocks in freecam.\nNOTE: Can only be used in singleplayer or with operator status.")
    public boolean noclip = true;

    @Comment("Prevents player movement while freecam is active.\nNOTE: Can only be used in singleplayer or with operator status.")
    public boolean freezePlayer = false;

    @Comment("Whether you can interact with blocks/entities in freecam.\nNOTE: Can only be used in singleplayer or with operator status.")
    public boolean allowInteract = false;

    @Comment("Disables freecam when damage is received.")
    public boolean disableOnDamage = true;

    @Comment("Shows your player in its original position.")
    public boolean showPlayer = true;

    @Comment("Whether you can see your hand in freecam.")
    public boolean showHand = false;

    @Comment("Notifies you when entering/exiting freecam.")
    public boolean notifyFreecam = true;

    @Comment("Notifies you when entering/exiting tripod cameras.")
    public boolean notifyPersistent = true;

    public enum FlightMode implements SelectionListEntry.Translatable {
        CREATIVE("Creative"),
        DEFAULT("Default");

        private final String name;

        FlightMode(String name) {
            this.name = name;
        }

        public String getKey() {
            return name;
        }
    }
}
