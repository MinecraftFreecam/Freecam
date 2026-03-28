package net.xolt.freecam.config.model;

import net.minecraft.world.level.block.Block;
import net.xolt.freecam.config.model.ModConfigDTO.CollisionConfig;

import static net.xolt.freecam.config.model.CollisionPredicateBuilder.builder;
import static net.xolt.freecam.config.model.CollisionPredicates.*;

@FunctionalInterface
public interface CollisionPredicate {

    boolean shouldIgnore(Block block);

    static CollisionPredicate create(CollisionConfig config) {
        if (config.ignoreAll) return IGNORE;

        CollisionPredicateBuilder builder = builder();
        if (config.ignoreTransparent) builder.matching(IGNORE_TRANSPARENT);
        if (config.ignoreOpenable) builder.matching(IGNORE_OPENABLE);
        if (config.ignoreCustom) builder.matching(whitelist(config.whitelist));
        return builder.build();
    }
}
