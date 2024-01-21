package net.xolt.freecam.config.keys;

import com.mojang.blaze3d.platform.InputConstants;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public class FreecamKeyMappingBuilder {
    private final String translationKey;
    private InputConstants.Type type = InputConstants.Type.KEYSYM;
    private int keyCode = GLFW_KEY_UNKNOWN;
    private Runnable action;
    private HoldAction holdAction;
    private long maxTicks = 10;

    private FreecamKeyMappingBuilder(String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Start building a {@link FreecamKeyMapping key} with the translation key provided.
     *
     * @param translationKey key to be appended onto {@code "key.freecam."}
     * @return a {@link FreecamKeyMapping} builder
     */
    public static FreecamKeyMappingBuilder builder(String translationKey) {
        return new FreecamKeyMappingBuilder(translationKey);
    }

    public FreecamKeyMappingBuilder type(InputConstants.Type type) {
        this.type = type;
        return this;
    }

    public FreecamKeyMappingBuilder maxHoldTicks(long ticks) {
        this.maxTicks = ticks;
        return this;
    }

    public FreecamKeyMappingBuilder defaultKey(int keyCode) {
        this.keyCode = keyCode;
        return this;
    }

    public FreecamKeyMappingBuilder action(Runnable action) {
        this.action = action;
        return this;
    }

    public FreecamKeyMappingBuilder holdAction(HoldAction action) {
        holdAction = action;
        return this;
    }

    /**
     * Build the {@link FreecamKeyMapping key mapping}.
     * <p>
     * If an {@link #action(Runnable) action} was defined, it will be run when the key is <strong>pressed</strong>.
     * <p>
     * If a {@link #holdAction(HoldAction) hold action} was defined, it will be run while the key is <strong>held</strong>
     * (each tick).
     * <p>
     * If both were defined, a {@link FreecamComboKeyMapping combo key} is provided where the {@link #holdAction(HoldAction) hold action}
     * is run as normal and the {@link #action(Runnable) action} is run when the key is <strong>released</strong>.
     * <br>
     * If the key was held for {@link #maxHoldTicks(long) max hold ticks} or longer (default 10) then {@link #action(Runnable) action}
     * is <strong>not run</strong>. It is also not run if any {@link #holdAction(HoldAction) hold action} returned
     * {@code true} since the key last released.
     * @return the {@link FreecamKeyMapping keybind}.
     */
    public FreecamKeyMapping build() {
        if (action != null && holdAction != null) {
            return new FreecamComboKeyMapping(translationKey, type, keyCode, action, holdAction, maxTicks);
        }
        if (action != null) {
            return new FreecamKeyMapping(translationKey, type, keyCode, self -> {
                while (self.consumeClick()) {
                    action.run();
                }
            });
        }
        if (holdAction != null) {
            return new FreecamKeyMapping(translationKey, type, keyCode, self -> {
                if (self.isDown()) {
                    holdAction.run();
                }
            });
        }
        throw new IllegalStateException("No action defined.");
    }
}
