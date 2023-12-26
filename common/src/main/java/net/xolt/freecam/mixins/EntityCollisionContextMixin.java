package net.xolt.freecam.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.xolt.freecam.util.EntityCollisionContextMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EntityCollisionContext.class)
public class EntityCollisionContextMixin implements EntityCollisionContextMixinInterface {

    private Entity entity;

    @Override
    public Optional<Entity> getEntity() {
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    private void onInit(Entity entity, CallbackInfo ci) {
        this.entity = entity;
    }
}
