package net.xolt.freecam.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.config.NewConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent ->
                Screen.hasShiftDown()
                ? NewConfig.getConfigScreen(parent)
                : AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
}
