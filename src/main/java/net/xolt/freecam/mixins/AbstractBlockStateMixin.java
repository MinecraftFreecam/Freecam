package net.xolt.freecam.mixins;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.CollisionWhitelist;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void onGetCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext && ((EntityShapeContext)context).getEntity() instanceof FreeCamera) {
            // Unless "Always Check Initial Collision" is on and Freecam isn't enabled yet
            if (!ModConfig.INSTANCE.collision.alwaysCheck || Freecam.isEnabled()) {
                // Ignore all collisions
                if (ModConfig.INSTANCE.collision.ignoreAll) {
                    cir.setReturnValue(VoxelShapes.empty());
                }
            }
            // Ignore transparent block collisions
            if (ModConfig.INSTANCE.collision.ignoreTransparent && CollisionWhitelist.isTransparent(getBlock())) {
                cir.setReturnValue(VoxelShapes.empty());
            }
            // Ignore transparent block collisions
            if (ModConfig.INSTANCE.collision.ignoreOpenable && CollisionWhitelist.isOpenable(getBlock())) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }
}
