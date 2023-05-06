package net.xolt.freecam.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.CollisionWhitelist;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiPredicate;

@Mixin(BlockCollisionSpliterator.class)
public class BlockCollisionSpliteratorMixin {

    @Shadow
    private Entity entity;

    // Apply custom block collision rules to freecam
    @Redirect(method = "offerBlockShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape onGetCollisionShape(BlockState blockState, BlockView world, BlockPos blockPos, ShapeContext context) {
        if (entity instanceof FreeCamera) {
            // Unless "Always Check Initial Collision" is on and Freecam isn't enabled yet
            if (!ModConfig.INSTANCE.collision.alwaysCheck || Freecam.isEnabled()) {
                // Ignore all collisions
                if (ModConfig.INSTANCE.collision.ignoreAll) {
                    return VoxelShapes.empty();
                }
            }
            // Ignore transparent block collisions
            if (ModConfig.INSTANCE.collision.ignoreTransparent && CollisionWhitelist.isTransparent(blockState.getBlock())) {
                return VoxelShapes.empty();
            }
            // Ignore transparent block collisions
            if (ModConfig.INSTANCE.collision.ignoreOpenable && CollisionWhitelist.isOpenable(blockState.getBlock())) {
                return VoxelShapes.empty();
            }
        }

        return blockState.getCollisionShape(world, blockPos, context);
    }
}
