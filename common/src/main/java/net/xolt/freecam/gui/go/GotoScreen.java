package net.xolt.freecam.gui.go;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.gui.textures.ScaledTexture;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.xolt.freecam.config.ModBindings.KEY_GOTO_GUI;

public class GotoScreen extends Screen {

    private static final ScaledTexture BACKGROUND = ScaledTexture.get(new ResourceLocation(Freecam.MOD_ID, "textures/gui/goto_background.png"));
    public static final int GRAY_COLOR = FastColor.ARGB32.color(255, 74, 74, 74);
    public static final int WHITE_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
    public static final int MIN_GUI_HEIGHT = 80;
    private static final int GUI_TOP = 50;
    private static final int GUI_WIDTH = 236;
    private static final int LIST_TOP = GUI_TOP + 8;

    private TargetsPane targets;
    private Button buttonBack;
    private Button buttonJump;
    private CycleButton<ModConfig.Perspective> buttonPerspective;
    private boolean initialized;

    public GotoScreen() {
        super(Component.translatable("gui.freecam.goto.title"));
    }

    @Override
    protected void init() {
        super.init();

        if (!initialized) {
            targets = new TargetsPane(LIST_TOP, width, height);

            buttonJump = Button.builder(Component.translatable("gui.freecam.goto.button.go"), button -> targets.gotoTarget())
                    .tooltip(Tooltip.create(Component.translatable("gui.freecam.goto.button.go.@Tooltip")))
                    .width(48)
                    .build();

            buttonPerspective = CycleButton
                    .builder((ModConfig.Perspective value) -> Component.translatable(value.getKey()))
                    .withValues(ModConfig.Perspective.values())
                    .withInitialValue(ModConfig.get().hidden.gotoPlayerPerspective)
                    .withTooltip(value -> Tooltip.create(Component.translatable("gui.freecam.goto.button.perspective.@Tooltip")))
                    .displayOnlyValue()
                    .create(0, 0, 80, 20, null, (button, value) -> {
                        ModConfig.get().hidden.gotoPlayerPerspective = value;
                        ModConfig.save();
                    });

            buttonBack = Button.builder(CommonComponents.GUI_BACK, button -> onClose()).width(48).build();
        }

        targets.setSize(width, getListHeight());

        int innerWidth = GUI_WIDTH - 10;
        int innerX = (width - innerWidth) / 2;
        FrameLayout positioner = new FrameLayout(innerX, LIST_TOP + getListHeight() + 3, innerWidth, 0);
        positioner.defaultChildLayoutSetting()
                .alignVerticallyBottom()
                .alignHorizontallyRight();
        LinearLayout layout = positioner.addChild(LinearLayout.horizontal());
        layout.defaultCellSetting()
                .alignVerticallyBottom()
                .paddingHorizontal(2);

        layout.addChild(buttonBack);
        layout.addChild(buttonPerspective);
        layout.addChild(buttonJump);

        positioner.arrangeElements();
        positioner.visitWidgets(this::addRenderableWidget);

        addRenderableWidget(targets);
        setInitialFocus(targets);

        initialized = true;
    }

    @Override
    public void renderBackground(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        super.renderBackground(gfx, mouseX, mouseY, delta);
        int left = (width - GUI_WIDTH) / 2;
        BACKGROUND.draw(gfx, left, GUI_TOP, GUI_WIDTH, getGuiHeight());
    }

    @Override
    public void tick() {
        super.tick();
        if (initialized) {
            targets.tick();
            buttonJump.active = targets.hasTarget();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (targets.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (CommonInputs.selected(keyCode)) {
            targets.gotoTarget();
            return true;
        }
        if (KEY_GOTO_GUI.matches(keyCode, scanCode) && !targets.isTyping()) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (Objects.equals(getFocused(), focused)) {
            return;
        }
        super.setFocused(focused);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // GUI height
    private int getGuiHeight() {
        return Math.max(MIN_GUI_HEIGHT, height - (GUI_TOP * 2));
    }

    // List window height
    private int getListHeight() {
        return getGuiHeight() - 29 - 8;
    }
}
