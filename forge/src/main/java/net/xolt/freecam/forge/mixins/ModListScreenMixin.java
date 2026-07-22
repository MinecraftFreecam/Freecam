package net.xolt.freecam.forge.mixins;

//~ if >=1.18 'fmlclient.gui.screen' -> 'client.gui'
import net.minecraftforge.client.gui.ModListScreen;
//~ if >=1.18 fmlclient -> client
import net.minecraftforge.client.gui.widget.ModListWidget;
//? if forge: >=41.1
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ModListScreen.class)
abstract class ModListScreenMixin {

    // When shadowing third-party mods, we merge them into our `mods.toml` file.
    // Unfortunately, FML only allows defining `license` per-file instead of per-mod.
    @Unique
    private static final Map<String, String> freecam$LICENSE_OVERRIDES = Map.of(
        "cloth_config", "GNU LGPLv3"
    );

    @Shadow(remap = false)
    private ModListWidget.ModEntry selected;

    //~ if forge: >=41.1 'forgespi/language/IModFileInfo' -> 'fml/loading/moddiscovery/ModFileInfo'
    @Redirect(method = "updateCache", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/loading/moddiscovery/ModFileInfo;getLicense()Ljava/lang/String;"), remap = false)
    //~ if forge: >=41.1 IModFileInfo -> ModFileInfo
    String onGetLicense(ModFileInfo info) {
        // When using Freecam's ModFileInfo, check if we need to override the selected mod's license
        if (info == freecam$getOurModFileInfo()) {
            String override = freecam$LICENSE_OVERRIDES.get(selected.getInfo().getModId());
            if (override != null) return override;
        }

        return info.getLicense();
    }

    @Unique
    private IModFileInfo freecam$getOurModFileInfo() {
        return ModList.get().getModFileById(Freecam.MOD_ID);
    }
}
