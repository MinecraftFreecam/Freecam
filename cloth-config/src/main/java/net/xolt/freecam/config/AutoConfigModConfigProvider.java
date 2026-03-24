package net.xolt.freecam.config;

//~ if cloth: >=21.11 'AutoConfig' -> 'AutoConfigClient'
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class AutoConfigModConfigProvider extends ModConfigProvider {

    public AutoConfigModConfigProvider() {}

    @Override
    public MCAwareModConfig getConfig() {
        return AutoConfigModConfig.INSTANCE;
    }

    @Override
    public void setupConfig() {
        AutoConfigModConfig.init();
    }

    @Override
    public Screen createConfigScreen(@Nullable Screen parent) {
        //~ if cloth: >=21.11 'AutoConfig.' -> 'AutoConfigClient.'
        return AutoConfigClient.getConfigScreen(AutoConfigModConfig.class, parent).get();
    }

}
