package net.xolt.freecam.config;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollisionWhitelist {

    private static final Predicate<Block> transparent = Builder.builder()
            .matching(AbstractGlassBlock.class, IronBarsBlock.class)
            .matching(BarrierBlock.class)
            .build();

    private static final Predicate<Block> openable = Builder.builder()
            .matching(FenceGateBlock.class)
            .matching(DoorBlock.class, TrapDoorBlock.class)
            .build();

    public static boolean isTransparent(Block block) {
        return transparent.test(block);
    }

    public static boolean isOpenable(Block block) {
        return openable.test(block);
    }

    private static String getBlockId(Block block) {
        return Registry.BLOCK.getKey(block).toString();
    }

    private static class Builder {
        private final Collection<Predicate<Block>> predicates = new ArrayList<>();

        private Builder() {}

        public static Builder builder() {
            return new Builder();
        }

        public final Builder matching(String... ids) {
            return matching(block -> Arrays.asList(ids).contains(getBlockId(block)));
        }

        public final Builder matching(Pattern... patterns) {
            return matching(block -> {
                String id = getBlockId(block);
                return Arrays.stream(patterns)
                        .map(pattern -> pattern.matcher(id))
                        .anyMatch(Matcher::find);
            });
        }

        @SafeVarargs
        public final Builder matching(Class<? extends Block>... classes) {
            return matching(block -> Arrays.stream(classes).anyMatch(clazz -> clazz.isInstance(block)));
        }

        public final Builder matching(Predicate<Block> predicate) {
            predicates.add(predicate);
            return this;
        }

        public Predicate<Block> build() {
            return block -> predicates.stream().anyMatch(predicate -> predicate.test(block));
        }
    }
}
