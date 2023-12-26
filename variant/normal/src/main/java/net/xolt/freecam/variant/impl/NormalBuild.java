package net.xolt.freecam.variant.impl;

import net.xolt.freecam.variant.api.BuildVariant;

public class NormalBuild implements BuildVariant {
    @Override
    public String name() {
        return "normal";
    }

    @Override
    public boolean cheatsPermitted() {
        return true;
    }
}
