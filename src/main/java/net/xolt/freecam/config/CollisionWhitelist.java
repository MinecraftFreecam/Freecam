package net.xolt.freecam.config;

import net.minecraft.block.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class CollisionWhitelist {

    private static final Collection<Class<? extends Block>> transparentWhitelist = Collections.unmodifiableList(Arrays.asList(
            AbstractGlassBlock.class,
            PaneBlock.class,
            BarrierBlock.class
    ));

    private static final Collection<Class<? extends Block>> openableWhitelist = Collections.unmodifiableList(Arrays.asList(
            FenceGateBlock.class,
            DoorBlock.class,
            TrapdoorBlock.class
    ));

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
