package net.xolt.freecam.environment;

import net.minecraft.server.Bootstrap;
import net.xolt.freecam.testing.extension.BootstrapMinecraft;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@BootstrapMinecraft
class BootstrapTest {
    @Test
    @DisplayName("Validate Minecraft is bootstrapped")
    void validateBootstrap() {
        Bootstrap.validate();
    }
}
