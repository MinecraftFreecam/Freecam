package net.xolt.freecam.util;

import net.minecraft.world.entity.Entity;

import java.util.Optional;

public interface EntityCollisionContextMixinInterface {

    Optional<Entity> getEntity();
}
