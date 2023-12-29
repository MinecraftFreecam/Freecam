package net.xolt.freecam.gui.go;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.gui.textures.ScaledTexture;
import net.xolt.freecam.gui.go.tabs.GotoScreenTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static net.xolt.freecam.Freecam.MC;

public class TargetsPane extends AbstractContainerWidget implements Tickable {

    private static final ScaledTexture BACKGROUND = ScaledTexture.get(new ResourceLocation(Freecam.MOD_ID, "textures/gui/goto_list_background.png"));
    private static final ResourceLocation SEARCH_ICON = new ResourceLocation("icon/search");
    private static final Component SEARCH_TEXT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    private static final int SEARCH_ICON_SIZE = 12;
    private static final int SEARCH_Y_OFFSET = 2;
    private static final int SEARCH_X_OFFSET = SEARCH_ICON_SIZE + 6;
    private static final int SEARCH_HEIGHT = 15;
    private static final int LIST_Y_OFFSET = SEARCH_Y_OFFSET + SEARCH_HEIGHT;
    private static final int LIST_ITEM_HEIGHT = 36;

    private final EditBox searchBox;
    private final TargetListWidget list;
    private final List<AbstractWidget> children;

    private GotoScreenTab currentTab;
    private String currentSearch;

    public TargetsPane(int top, int width, int height, GotoScreenTab tab) {
        super(0, top, width, height, Component.empty());

        this.currentTab = tab;
        this.list = new TargetListWidget(top + LIST_Y_OFFSET, width, height - LIST_Y_OFFSET - 1, LIST_ITEM_HEIGHT);
        this.searchBox = new EditBox(MC.font, list.getRowWidth() - SEARCH_X_OFFSET - 1, SEARCH_HEIGHT, SEARCH_TEXT);
        this.searchBox.setPosition(renderX() + SEARCH_X_OFFSET, getY() + SEARCH_Y_OFFSET);
        this.searchBox.setHint(SEARCH_TEXT);
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setResponder(search -> currentSearch = search.trim().toLowerCase(Locale.ROOT));

        this.children = List.of(searchBox, list);

        this.setFocused(list);

        // Prevent slight delay in showing player list
        this.tick();
    }

    @Override
    protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        BACKGROUND.draw(gfx, renderX(), getY(), renderWidth(), getHeight());
        gfx.blitSprite(SEARCH_ICON, renderX() + 3, getY() + 4, SEARCH_ICON_SIZE, SEARCH_ICON_SIZE);
        children.forEach(widget -> widget.render(gfx, mouseX, mouseY, partialTick));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        children.forEach(widget -> widget.updateNarration(narrationElementOutput));
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public void tick() {
        list.replaceEntries(currentTab.provideEntriesFor(list).stream()
                .filter(entry -> currentSearch == null
                        || currentSearch.isEmpty()
                        || entry.matchesSearch(currentSearch))
                .sorted()
                .toList());
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        searchBox.setX(renderX() + SEARCH_X_OFFSET);
        list.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        list.setHeight(height - LIST_Y_OFFSET - 1);
    }

    @Override
    public void setX(int x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        searchBox.setY(y + SEARCH_Y_OFFSET);
        list.setY(y + LIST_Y_OFFSET);
    }

    @Override
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (Objects.equals(getFocused(), focused)) {
            return;
        }
        super.setFocused(focused);
    }

    @Override
    public void setFocused(boolean focused) {
        if (focused) {
            // Ensure something is focused
            if (getFocused() == null) {
                setFocused(list);
            }
        } else {
            // Remove focus from child
            setFocused(null);
        }
        super.setFocused(focused);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }

        if (searchBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setFocused(list);
                return true;
            }
            return searchBox.keyPressed(keyCode, scanCode, modifiers);
        }

        if (list.isFocused()) {
            return list.keyPressed(keyCode, scanCode, modifiers);
        }

        return false;
    }

    private int renderWidth() {
        return list.getRowWidth() + 2;
    }

    private int renderX() {
        return (width - renderWidth()) / 2;
    }

    public void setTab(GotoScreenTab tab) {
        this.currentTab = tab;
    }

    /**
     * @return whether a text entry widget is in focus.
     */
    public boolean isTyping() {
        return searchBox.isFocused();
    }

    /**
     * @return whether a goto target is currently selected.
     */
    public boolean hasTarget() {
        return list.getSelected() != null;
    }

    /**
     * Calls {@link TargetListEntry#go()} on the current target, if there is one.
     */
    public void gotoTarget() {
        Optional.ofNullable(list.getSelected()).ifPresent(TargetListEntry::go);
    }
}
