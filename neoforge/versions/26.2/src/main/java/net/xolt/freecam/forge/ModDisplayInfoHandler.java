package net.xolt.freecam.forge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.modlist.ModDisplayInfo;
import net.xolt.freecam.Freecam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = Freecam.MOD_ID, dist = Dist.CLIENT)
public class ModDisplayInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("Freecam/ModDisplayInfo");

    public ModDisplayInfoHandler(ModContainer container) {
        try {
            container.registerExtensionPoint(ModDisplayInfo.class, new ModDisplayInfoCompat(container));
        } catch (NoClassDefFoundError _) {
            // Before 26.2.0.50-beta, NeoForge did not have a ModDisplayInfo class
            LOGGER.warn("Unable to register ModDisplayInfo compatability extension. Consider updating NeoForge.");
        }
    }
}
