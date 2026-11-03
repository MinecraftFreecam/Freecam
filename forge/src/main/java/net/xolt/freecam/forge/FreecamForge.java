package net.xolt.freecam.forge;

import net.minecraftforge.fml.common.Mod;
import net.xolt.freecam.Freecam;

@Mod(Freecam.MOD_ID)
public class FreecamForge {
    public FreecamForge() {
        ForgeServerPolicyNetworking.register();
    }
}
