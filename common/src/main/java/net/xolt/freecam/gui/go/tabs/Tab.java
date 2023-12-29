package net.xolt.freecam.gui.go.tabs;

import net.minecraft.client.gui.components.AbstractButton;
import net.xolt.freecam.gui.go.TargetListEntry;
import net.xolt.freecam.gui.go.TargetListWidget;

import java.util.List;

interface Tab {

    List<TargetListEntry> provideEntriesFor(TargetListWidget widget);

    List<AbstractButton> extraButtons();
}
