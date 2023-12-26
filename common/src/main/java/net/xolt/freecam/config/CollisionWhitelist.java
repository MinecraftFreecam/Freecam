package net.xolt.freecam.config;

import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import java.util.Collection;
import java.util.List;

public class CollisionWhitelist {

    private static final Collection<Class<? extends Block>> transparentWhitelist = List.of(
            TransparentBlock.class,
            IronBarsBlock.class,
            BarrierBlock.class);

    private static final Collection<Class<? extends Block>> openableWhitelist = List.of(
            FenceGateBlock.class,
            DoorBlock.class,
            TrapDoorBlock.class);

    public static boolean isTransparent(Block block) {
        return isMatch(block, transparentWhitelist);
    }

    public static boolean isOpenable(Block block) {
        return isMatch(block, openableWhitelist);
    }

    private static boolean isMatch(Block block, Collection<Class<? extends Block>> whitelist) {
        return whitelist.stream().anyMatch(blockClass -> blockClass.isInstance(block));
    }
}
