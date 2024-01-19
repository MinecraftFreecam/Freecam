package net.xolt.freecam.environment;

import net.xolt.freecam.testing.extension.BootstrapMinecraft;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.MixinEnvironment;

@BootstrapMinecraft
class MixinTest {
    @Test
    @DisplayName("Audit mixin environment")
    void mixinEnvironmentAudit() {
        MixinEnvironment.getCurrentEnvironment().audit();
    }
}
