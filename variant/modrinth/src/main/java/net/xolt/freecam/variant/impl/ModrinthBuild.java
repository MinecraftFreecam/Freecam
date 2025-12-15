package net.xolt.freecam.variant.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.server.permissions.Permissions;
import net.xolt.freecam.variant.api.BuildVariant;

public class ModrinthBuild implements BuildVariant {

    private static final Minecraft MC = Minecraft.getInstance();

    @Override
    public String name() {
        return "modrinth";
    }

    @Override
    public boolean cheatsPermitted() {
        return MC.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) || MC.player.isCreative() || MC.hasSingleplayerServer();
    }
}
