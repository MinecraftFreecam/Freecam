package net.xolt.freecam.config.model;

//~ if >= 1.19 '.Registry' -> '.registries.BuiltInRegistries'
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CollisionPredicateBuilder {

    private final Collection<CollisionPredicate> predicates;

    private CollisionPredicateBuilder() {
        predicates = new ArrayList<>();
    }

    public static CollisionPredicateBuilder builder() {
        return new CollisionPredicateBuilder();
    }

    public final CollisionPredicateBuilder matching(String... ids) {
        return matching(block -> Arrays.asList(ids).contains(getBlockId(block)));
    }

    public final CollisionPredicateBuilder matching(Pattern... patterns) {
        return matching(block -> {
            String id = getBlockId(block);
            return Arrays.stream(patterns)
                    .map(pattern -> pattern.matcher(id))
                    .anyMatch(Matcher::find);
        });
    }

    @SafeVarargs
    public final CollisionPredicateBuilder matching(Class<? extends Block>... classes) {
        return matching(block -> Arrays.stream(classes).anyMatch(clazz -> clazz.isInstance(block)));
    }

    public final CollisionPredicateBuilder matching(CollisionPredicate predicate) {
        predicates.add(predicate);
        return this;
    }

    public CollisionPredicate build() {
        return block -> predicates.stream().anyMatch(predicate -> predicate.shouldIgnore(block));
    }

    private static String getBlockId(Block block) {
        //~ if >= 1.19 'Registry' -> 'BuiltInRegistries'
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }
}
