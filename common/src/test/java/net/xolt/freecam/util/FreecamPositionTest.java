package net.xolt.freecam.util;

import net.minecraft.client.player.RemotePlayer;
import net.xolt.freecam.test.extension.BootstrapMinecraft;
import org.joml.Quaternionf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static net.xolt.freecam.test.util.TestUtils.getFieldValue;
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
    @DisplayName("setRotation correctly updates quaternion")
    void setRotation_Quaternion() {
        Quaternionf initialRotation = getFieldValue(Quaternionf.class, position, "rotation");
        assertThat(initialRotation).as("Initially all zeros").satisfies(
                rotation -> assertThat(rotation.x).as("x is correct").isEqualTo(0),
                rotation -> assertThat(rotation.y).as("y is correct").isEqualTo(0),
                rotation -> assertThat(rotation.z).as("z is correct").isEqualTo(0),
                rotation -> assertThat(rotation.w).as("w is correct").isEqualTo(1)
        );
        position.setRotation(10, 20);
        Quaternionf rotationAfterTenTwenty = getFieldValue(Quaternionf.class, position, "rotation");
        assertThat(rotationAfterTenTwenty).as("Mutated correctly").satisfies(
                // Magic numbers obtained from working implementation
                rotation -> assertThat(rotation.x).as("x is correct").isEqualTo(0.1729874f),
                rotation -> assertThat(rotation.y).as("y is correct").isEqualTo(-0.08583164f),
                rotation -> assertThat(rotation.z).as("z is correct").isEqualTo(0.015134435f),
                rotation -> assertThat(rotation.w).as("w is correct").isEqualTo(0.98106027f)
        );
        position.setRotation(100, 200);
        Quaternionf rotationAfterHundredTwoHundred = getFieldValue(Quaternionf.class, position, "rotation");
        assertThat(rotationAfterHundredTwoHundred).as("Mutated correctly").satisfies(
                // Magic numbers obtained from working implementation
                rotation -> assertThat(rotation.x).as("x is correct").isEqualTo(0.63302225f),
                rotation -> assertThat(rotation.y).as("y is correct").isEqualTo(0.13302235f),
                rotation -> assertThat(rotation.z).as("z is correct").isEqualTo(0.7544065f),
                rotation -> assertThat(rotation.w).as("w is correct").isEqualTo(-0.11161902f)
        );
        position.setRotation(0, 0);
        Quaternionf rotationReset = getFieldValue(Quaternionf.class, position, "rotation");
        assertThat(rotationReset).as("Reset matches initial value").isEqualTo(initialRotation);

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