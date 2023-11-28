package net.xolt.freecam.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.xolt.freecam.Freecam;

public class FreecamFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Freecam.init();
    }
}
