package net.xolt.freecam.util;

import net.minecraft.client.player.RemotePlayer;
import net.xolt.freecam.test.extension.BootstrapMinecraft;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@BootstrapMinecraft
@ExtendWith(MockitoExtension.class)
class FreecamPositionTest {

    @Mock RemotePlayer player;
    private FreecamPosition position;

    @BeforeEach
    void setUp() {
        position = new FreecamPosition(player);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setRotation() {
        position.setRotation(10, 20);
        Assertions.assertAll(
                () -> Assertions.assertEquals(10, position.yaw),
                () -> Assertions.assertEquals(20, position.pitch));
    }

    @Test
    void mirrorRotation() {
    }

    @Test
    void moveForward() {
    }

    @Test
    void move() {
    }

    @Test
    void getChunkPos() {
    }
}