package net.xolt.freecam.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.CollisionWhitelist;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.EntityCollisionContextMixinInterface;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.variant.api.BuildVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow public abstract Block getBlock();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void onGetCollisionShape(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityCollisionContext entityShapeContext
                && ((EntityCollisionContextMixinInterface)entityShapeContext).getEntity().isPresent()
                && ((EntityCollisionContextMixinInterface)entityShapeContext).getEntity().get() instanceof FreeCamera) {
            // Return early if "Always Check Initial Collision" is on and Freecam isn't enabled yet
            if (ModConfig.INSTANCE.collision.alwaysCheck && !Freecam.isEnabled()) {
                return;
            }
            // Ignore all collisions
            if (ModConfig.INSTANCE.collision.ignoreAll && BuildVariant.getInstance().cheatsPermitted()) {
                cir.setReturnValue(Shapes.empty());
            }
            // Ignore transparent block collisions
            if (ModConfig.INSTANCE.collision.ignoreTransparent && CollisionWhitelist.isTransparent(getBlock())) {
                cir.setReturnValue(Shapes.empty());
            }
            // Ignore openable block collisions
            if (ModConfig.INSTANCE.collision.ignoreOpenable && CollisionWhitelist.isOpenable(getBlock())) {
                cir.setReturnValue(Shapes.empty());
            }
        }
    }
}
