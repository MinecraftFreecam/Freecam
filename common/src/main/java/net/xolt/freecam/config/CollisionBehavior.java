package net.xolt.freecam.config;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.*;
//? if > 1.18.2 {
import net.minecraft.core.registries.BuiltInRegistries;
//? } else {
/*import net.minecraft.core.Registry;
*///? }

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollisionBehavior {

    private static final Predicate<Block> transparent = Builder.builder()
            //? if >=1.20.6 {
            .matching(TransparentBlock.class)
            //? } else
            //.matching(AbstractGlassBlock.class)
            .matching(IronBarsBlock.class)
            .matching(BarrierBlock.class)
            .build();

    private static final Predicate<Block> openable = Builder.builder()
            .matching(FenceGateBlock.class)
            .matching(DoorBlock.class, TrapDoorBlock.class)
            .build();

    private static Predicate<Block> custom = block -> false;

    @SuppressWarnings("RedundantIfStatement")
    public static boolean isIgnored(Block block) {
        if (ModConfig.INSTANCE.collision.ignoreAll) {
            return true;
        }

        if (ModConfig.INSTANCE.collision.ignoreTransparent && transparent.test(block)) {
            return true;
        }

        if (ModConfig.INSTANCE.collision.ignoreOpenable && openable.test(block)) {
            return true;
        }

        if (ModConfig.INSTANCE.collision.ignoreCustom && custom.test(block)) {
            return true;
        }

        return false;
    }

    static InteractionResult onConfigChange(ConfigHolder<ModConfig> holder, ModConfig config) {
        String[] ids = config.collision.whitelist.ids.stream()
                .map(id -> id.contains(":") ? id : "minecraft:" + id)
                .toArray(String[]::new);

        Pattern[] patterns = config.collision.whitelist.patterns.stream()
                .map(Pattern::compile)
                .toArray(Pattern[]::new);

        custom = Builder.builder()
                .matching(ids)
                .matching(patterns)
                .build();

        return InteractionResult.PASS;
    }

    private static String getBlockId(Block block) {
        return
                //? if > 1.18.2 {
                BuiltInRegistries
                //? } else
                //Registry
                        .BLOCK.getKey(block).toString();
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
