package net.xolt.freecam.mixins;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.xolt.freecam.util.EntityShapeContextMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityShapeContext.class)
public abstract class EntityShapeContextMixin implements EntityShapeContextMixinInterface {

    private Entity entity;

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", at = @At("RETURN"))
    private void onInit(Entity entity, CallbackInfo ci) {
        this.entity = entity;
    }
}
