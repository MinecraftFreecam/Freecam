package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.xolt.freecam.testing.extension.BootstrapMinecraft;
import net.xolt.freecam.testing.extension.EnableMockito;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableMockito
@BootstrapMinecraft
class FreecamPositionTest {

    Entity entity;
    FreecamPosition position;

    static double[] distances() {
        return new double[] { 1, -1, 2_000_000_001, 0.00456789, -0.0000445646456456060456, 2.5 };
    }

    @BeforeEach
    void setUp() {
        ClientLevel level = mock(ClientLevel.class);
        when(level.getSharedSpawnPos()).thenReturn(BlockPos.ZERO);
        when(level.getSharedSpawnAngle()).thenReturn(0f);
        GameProfile profile = new GameProfile(new UUID(0, 0), "TestPlayer");
        entity = new RemotePlayer(level, profile);
        position = new FreecamPosition(entity);
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @EnumSource(Pose.class)
    @DisplayName("Adjust entity position for pose")
    void init_pose(Pose pose) {
        entity.setPose(pose);
        double diff = entity.getEyeHeight(pose) - entity.getEyeHeight(Pose.SWIMMING);
        FreecamPosition swimPos = new FreecamPosition(entity);

        assertThat(swimPos.x).as("x is %01.2f".formatted(entity.getX())).isEqualTo(entity.getX());
        assertThat(swimPos.y).as("y is %01.2f higher than %01.2f".formatted(diff, entity.getY())).isEqualTo(entity.getY() + diff, withPrecision(0.0000004));
        assertThat(swimPos.z).as("z is %01.2f".formatted(entity.getZ())).isEqualTo(entity.getZ());
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