package net.xolt.freecam.config;

import net.minecraft.world.level.block.*;
//~ if >= 1.19 '.Registry' -> '.registries.BuiltInRegistries'
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CollisionBehavior {

    private AutoConfigModConfig.CollisionConfig config;

    private static final Predicate<Block> transparent = Builder.builder()
            //~ if >=1.20.6 'AbstractGlassBlock' -> 'TransparentBlock'
            .matching(TransparentBlock.class)
            .matching(IronBarsBlock.class)
            .matching(BarrierBlock.class)
            .build();

    private final Predicate<Block> openable = Builder.builder()
            .matching(FenceGateBlock.class)
            .matching(DoorBlock.class, TrapDoorBlock.class)
            .build();

    private Predicate<Block> custom = block -> false;

    CollisionBehavior(AutoConfigModConfig.CollisionConfig config) {
        rebuild(config);
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean isIgnored(Block block) {
        if (config.ignoreAll) {
            return true;
        }

        if (config.ignoreTransparent && transparent.test(block)) {
            return true;
        }

        if (config.ignoreOpenable && openable.test(block)) {
            return true;
        }

        if (config.ignoreCustom && custom.test(block)) {
            return true;
        }

        return false;
    }

    void rebuild(AutoConfigModConfig.CollisionConfig config) {
        String[] ids = config.whitelist.ids.stream()
                .map(id -> id.contains(":") ? id : "minecraft:" + id)
                .toArray(String[]::new);

        Pattern[] patterns = config.whitelist.patterns.stream()
                .map(Pattern::compile)
                .toArray(Pattern[]::new);

        custom = Builder.builder()
                .matching(ids)
                .matching(patterns)
                .build();

        this.config = config;
    }

    private static String getBlockId(Block block) {
        //~ if >= 1.19 'Registry' -> 'BuiltInRegistries'
        return BuiltInRegistries.BLOCK.getKey(block).toString();
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
