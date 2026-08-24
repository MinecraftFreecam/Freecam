package net.xolt.freecam.forge;

import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.modlist.DefaultModDisplayInfo;

public class ModDisplayInfoCompat extends DefaultModDisplayInfo {

    public ModDisplayInfoCompat(ModContainer container) {
        super(container);
    }

    /**
     * NeoForge 26.2.0.50-beta introduced the new ModListScreen and associated ModDisplayInfo,
     * however description translations were initially broken.
     * <p>
     * This overrides {@link DefaultModDisplayInfo#description()} to backport the fix from 26.0.66
     * to earlier versions.
     *
     * @see <a href="https://github.com/neoforged/NeoForge/pull/3407">#3407</a>
     */
    @Override
    public Component description() {
        return Component.translatableWithFallback("neoforge.screen.mods.info.description." + id(), container().getModInfo().getDescription());
    }
}
