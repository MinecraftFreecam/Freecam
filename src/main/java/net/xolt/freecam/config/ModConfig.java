package net.xolt.freecam.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

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

    @Comment("Whether you can travel through blocks in freecam.")
    public boolean noclip = true;

    @Comment("Whether you can interact with blocks/entities in freecam.")
    public boolean allowInteract = false;

    @Comment("Whether taking damage disables freecam.")
    public boolean disableOnDamage = true;

    @Comment("The initial perspective of the camera.")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public Perspective perspective = Perspective.THIRD_PERSON;

    @Comment("Whether your player is shown in your original position.")
    public boolean showPlayer = true;

    @Comment("Whether you can see your hand in freecam.")
    public boolean showHand = false;

    @Comment("Whether action bar notifications are displayed.")
    public boolean notify = true;

    public enum FlightMode implements SelectionListEntry.Translatable {
        CREATIVE("Creative"),
        DEFAULT("Default");

        private final String name;

        FlightMode(String name) {
            this.name = name;
        }

        public @NotNull String getKey() {
            return name;
        }
    }

    public enum Perspective implements SelectionListEntry.Translatable {
        FIRST_PERSON("First Person"),
        THIRD_PERSON("Third Person"),
        THIRD_PERSON_MIRROR("Mirror"),
        INSIDE("Inside");

        private final String name;

        Perspective(String name) {
            this.name = name;
        }

        public @NotNull String getKey() {
            return name;
        }
    }
}
