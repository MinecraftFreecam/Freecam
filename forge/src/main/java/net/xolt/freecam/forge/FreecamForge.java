package net.xolt.freecam.forge;

import net.minecraftforge.fml.common.Mod;

@Mod("freecam")
public class FreecamForge {
    public FreecamForge() {
        ForgeServerPolicyNetworking.register();
    }
}
