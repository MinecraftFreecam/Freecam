package net.xolt.freecam.config.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class FreecamKeyMapping extends KeyMapping implements Tickable {

    //? if >=1.21.11 {
    private static final Category FREECAM_CATEGORY = Category.register(Identifier.fromNamespaceAndPath("freecam", "freecam"));
    //? } else
    //private static final String FREECAM_CATEGORY = "key.category.freecam.freecam";

    private final Consumer<FreecamKeyMapping> onTick;

    /**
     * @apiNote should only be used if overriding {@link #tick()}
     */
    protected FreecamKeyMapping(String translationKey, InputConstants.Type type, int code) {
        this(translationKey, type, code, null);
    }

    FreecamKeyMapping(String translationKey, InputConstants.Type type, int code, Consumer<FreecamKeyMapping> onTick) {
        super("key.freecam." + translationKey, type, code, FREECAM_CATEGORY);
        this.onTick = onTick;
    }

    @Override
    public void tick() {
        onTick.accept(this);
    }

    /**
     * Reset whether the key was pressed.
     *
     * @implNote Cannot use {@link KeyMapping#release()} because it doesn't work as expected.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public void reset() {
        while (consumeClick()) {}
    }
}
