package net.xolt.freecam.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.xolt.freecam.config.ConfigScreenProvider;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        Optional<ConfigScreenFactory<?>> factory = ConfigScreenProvider.provider()
                .map(provider -> provider::getConfigScreen);
        return factory.orElseGet(ModMenuApi.super::getModConfigScreenFactory);
    }
}
