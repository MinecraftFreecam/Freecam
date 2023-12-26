package net.xolt.freecam.variant.impl;

import net.minecraft.client.Minecraft;
import net.xolt.freecam.variant.api.BuildVariant;

public class ModrinthBuild implements BuildVariant {

    private static final Minecraft MC = Minecraft.getInstance();

    @Override
    public String name() {
        return "modrinth";
    }

    @Override
    public boolean cheatsPermitted() {
        return MC.player.hasPermissions(2) || MC.player.isCreative() || MC.hasSingleplayerServer();
    }
}
