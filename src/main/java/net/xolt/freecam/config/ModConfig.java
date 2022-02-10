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

    @Comment("Changes the horizontal speed of freecam.")
    public double horizontalSpeed = 1.0;

    @Comment("Changes the vertical speed of freecam.")
    public double verticalSpeed = 0.8;

    @Comment("Toggles whether you can break blocks while freecam is enabled.")
    public boolean allowBlockBreak = true;

    @Comment("Toggles whether you can interact with entities while freecam is enabled.")
    public boolean allowEntityInteract = true;

    @Comment("Toggles whether your player is rendered in your original position while freecam is enabled.")
    public boolean showPlayer = true;

    @Comment("Toggles whether your hand is shown while freecam is enabled.")
    public boolean showHand = false;

    @Comment("Toggles action bar notifications.")
    public boolean notify = true;

    @Comment("The message that is shown when freecam is enabled.")
    public String enableMessage = "Freecam has been enabled.";

    @Comment("The message that is shown when freecam is disabled.")
    public String disableMessage = "Freecam has been disabled.";
}
