package net.xolt.freecam.util;

import net.minecraft.client.player.RemotePlayer;
import net.xolt.freecam.testing.extension.BootstrapMinecraft;
import net.xolt.freecam.testing.extension.EnableMockito;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@EnableMockito
@BootstrapMinecraft
class FreecamPositionTest {

    @Mock RemotePlayer player;
    private FreecamPosition position;

    static double[] distances() {
        return new double[] { 1, -1, 2_000_000_001, 0.00456789, -0.0000445646456456060456, 2.5 };
    }

    @BeforeEach
    void setUp() {
        position = new FreecamPosition(player);
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @MethodSource("distances")
    @DisplayName("Moves forward on x axis")
    void moveForward_x(double distance) {
        float yaw = -90;
        float pitch = 0;

        double x = position.x;
        double y = position.y;
        double z = position.z;

        position.setRotation(yaw, pitch);
        position.moveForward(distance);

        assertThat(position.x).as("x increased by " + distance).isEqualTo(x + distance);
        assertThat(position.y).as("y is unchanged").isEqualTo(y);
        assertThat(position.z).as("z is unchanged").isEqualTo(z);
    }

    @ParameterizedTest
    @MethodSource("distances")
    @DisplayName("Moves forward on y axis")
    void moveForward_y(double distance) {
        float yaw = 0;
        float pitch = -90;

        double x = position.x;
        double y = position.y;
        double z = position.z;

        position.setRotation(yaw, pitch);
        position.moveForward(distance);

        assertThat(position.x).as("x is unchanged").isEqualTo(x);
        assertThat(position.y).as("y increased by " + distance).isEqualTo(y + distance);
        assertThat(position.z).as("z is unchanged").isEqualTo(z);
    }

    @ParameterizedTest
    @MethodSource("distances")
    @DisplayName("Moves forward on z axis")
    void moveForward_z(double distance) {
        float yaw = 0;
        float pitch = 0;

        double x = position.x;
        double y = position.y;
        double z = position.z;

        position.setRotation(yaw, pitch);
        position.moveForward(distance);

        assertThat(position.x).as("x is unchanged").isEqualTo(x);
        assertThat(position.y).as("y is unchanged").isEqualTo(y);
        assertThat(position.z).as("z increased by " + distance).isEqualTo(z + distance);
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
    void getChunkPos() {
    }
}