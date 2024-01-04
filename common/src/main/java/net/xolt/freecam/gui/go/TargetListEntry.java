package net.xolt.freecam.gui.go;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.util.FreeCamera;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.xolt.freecam.gui.go.GotoScreen.GRAY_COLOR;

public abstract class TargetListEntry extends ObjectSelectionList.Entry<TargetListEntry> implements Comparable<TargetListEntry> {

    private static final long DOUBLE_CLICK_MILLIS = 250;

    protected final Minecraft mc;
    private final TargetListWidget parent;
    private long lastClicked;

    protected TargetListEntry(Minecraft mc, TargetListWidget parent) {
        this.mc = mc;
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics gfx, int index, int y, int x, int fullEntryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // We are passed a reduced entryHeight but the full entryWidth...
        int entryWidth = fullEntryWidth - 4;
        gfx.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        long time = Util.getMillis();
        parent.setSelected(this);
        parent.setFocused(this);
        if (time - lastClicked < DOUBLE_CLICK_MILLIS) {
            go();
        }
        lastClicked = time;
        return true;
    }

    @Override
    public @NotNull Component getNarration() {
        return Component.literal(getName());
    }

    @Override
    public int compareTo(@NotNull TargetListEntry entry) {
        return getName().compareTo(entry.getName());
    }

    public boolean matchesSearch(String string) {
        return getName().toLowerCase(Locale.ROOT).contains(string);
    }

    public abstract String getName();

    /**
     * Close the current {@link Screen} and move {@link FreeCamera} to this target.
     */
    public abstract void go();
}
