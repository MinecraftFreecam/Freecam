package net.xolt.freecam.config.model;

import net.xolt.freecam.network.ServerPolicy;

/** Settings for servers hosted by this installation, independent of received restrictions. */
public class ServerPolicyConfig {
    public boolean allowFreecam = true;
    public boolean allowClipping = true;
    public boolean allowFullbright = true;
    public boolean allowInteract = true;

    public ServerPolicy snapshot() {
        return new ServerPolicy(allowFreecam, allowClipping, allowFullbright, allowInteract);
    }
}
