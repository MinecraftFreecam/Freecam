package net.xolt.freecam.util;

import net.minecraft.client.player.RemotePlayer;
import net.xolt.freecam.test.extension.BootstrapMinecraft;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

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

    @ParameterizedTest
    @DisplayName("setRotation correctly sets yaw & pitch")
    @ValueSource(floats = { -16.456f, 0, 10, 2.5f, 2000008896.546f })
    void setRotation_YawPitch(float number) {
        final float constant = 10;
        assertThat(position).isNotNull().satisfies(
                position -> {
                    position.setRotation(number, constant);
                    assertThat(position).as("Yaw is set correctly").satisfies(
                            p -> assertThat(p.yaw).as("Yaw is set to (var) " + number).isEqualTo(number),
                            p -> assertThat(p.pitch).as("Pitch is set to (const) " + constant).isEqualTo(constant)
                    );
                },
                position -> {
                    position.setRotation(constant, number);
                    assertThat(position).as("Pitch is set correctly").satisfies(
                            p -> assertThat(p.yaw).as("Yaw is set to (const) " + constant).isEqualTo(constant),
                            p -> assertThat(p.pitch).as("Pitch is set to (var) " + number).isEqualTo(number)
                    );
                }
        );
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