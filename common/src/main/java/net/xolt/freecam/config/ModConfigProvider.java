package net.xolt.freecam.config;

import net.xolt.freecam.util.SingleInstanceServiceLoader;

public interface ModConfigProvider {

    MCAwareModConfig getConfig();
    void setupConfig();

    static void init() {
        Holder.INSTANCE.setupConfig();
    }

    static MCAwareModConfig instance() {
        return Holder.INSTANCE.getConfig();
    }

    class Holder {
        private Holder() {}

        private static final ModConfigProvider INSTANCE = SingleInstanceServiceLoader.get(ModConfigProvider.class);
    }
}
