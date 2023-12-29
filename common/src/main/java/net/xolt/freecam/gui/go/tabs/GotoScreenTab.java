package net.xolt.freecam.gui.go.tabs;

import net.minecraft.client.gui.components.AbstractButton;
import net.xolt.freecam.gui.go.TargetListEntry;
import net.xolt.freecam.gui.go.TargetListWidget;

import java.util.List;

public enum GotoScreenTab implements Tab {
    PLAYER(new PlayerTab());

    private final Tab implementation;

    GotoScreenTab(Tab tab) {
        this.implementation = tab;
    }

    @Override
    public List<TargetListEntry> provideEntriesFor(TargetListWidget widget) {
        return implementation.provideEntriesFor(widget);
    }

    @Override
    public List<AbstractButton> extraButtons() {
        return implementation.extraButtons();
    }
}
