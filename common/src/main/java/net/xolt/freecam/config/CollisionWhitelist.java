package net.xolt.freecam.config;

import net.minecraft.block.*;

import java.util.Collection;
import java.util.List;

public class CollisionWhitelist {

    private static final Collection<Class<? extends Block>> transparentWhitelist = List.of(
            TransparentBlock.class,
            PaneBlock.class,
            BarrierBlock.class);

    private static final Collection<Class<? extends Block>> openableWhitelist = List.of(
            FenceGateBlock.class,
            DoorBlock.class,
            TrapdoorBlock.class);

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
