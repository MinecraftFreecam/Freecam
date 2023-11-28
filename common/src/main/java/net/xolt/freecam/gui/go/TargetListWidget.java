package net.xolt.freecam.gui.go;

import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;

import static net.xolt.freecam.Freecam.MC;

/**
 * A GUI widget displaying a list of {@link TargetListEntry}s.
 */
public class TargetListWidget extends ObjectSelectionList<TargetListEntry> {

    public TargetListWidget(int top, int width, int height, int itemHeight) {
        super(MC, width, height, top, itemHeight);
        this.setRenderBackground(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        TargetListEntry entry = getSelected();
        return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
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
            // When the list gains focus, try to focus an entry
            if (getFocused() == null) {
                setFocused(getSelected());
            }
        } else {
            // When the list looses focus, remove focus from entry
            setFocused(null);
        }
        super.setFocused(focused);
    }

    /**
     * Replace the list's content with new {@link TargetListEntry} entries.
     *
     * <ul>
     *     <li> If the existing selection is present in the new list, it is selected.
     *     <li> If not, then the existing selection's nearest neighbor is selected.
     *     <li> If no existing entries are present in the new list, then the first entry is selected.
     *     <li> If the list is empty, then nothing is selected.
     * </ul>
     *
     * @param targets the new list of {@link TargetListEntry} entries.
     */
    @Override
    public void replaceEntries(Collection<TargetListEntry> targets) {
        if (Objects.equals(children(), targets)) {
            // Update only if the list has changed
            return;
        }
        TargetListEntry selection = migrateSelection(getSelected(), targets, children());
        super.replaceEntries(targets);
        setSelected(selection);
    }

    private static @Nullable TargetListEntry migrateSelection(TargetListEntry selection, Collection<TargetListEntry> newEntries, Collection<TargetListEntry> oldEntries) {
        // New list is empty, can't select anything
        if (newEntries.isEmpty()) {
            return null;
        }

        // No previous selection existed, nothing to check
        if (selection == null) {
            return newEntries.stream().findFirst().orElse(null);
        }

        // Migrate to the nearest neighbor of the previous selection
        return nearestNeighbor(selection, newEntries, oldEntries);
    }

    // Search for the "nearest neighbor" still present in the new list.
    // This minimises GUI focus jumps when the selection is lost, improving UX.
    private static @Nullable TargetListEntry nearestNeighbor(TargetListEntry selection, Collection<TargetListEntry> newEntries, Collection<TargetListEntry> oldEntries) {
        // Need a list to access by index, avoid creating a new one if we already have one
        List<TargetListEntry> newList = newEntries instanceof List<TargetListEntry> list ? list : newEntries.stream().toList();
        List<TargetListEntry> oldList = oldEntries instanceof List<TargetListEntry> list ? list : oldEntries.stream().toList();

        int len = oldList.size();
        int index = oldList.indexOf(selection);
        IntPredicate newContains = i -> newList.contains(oldList.get(i));

        // Check if the previous selection is still present
        if (index >= 0 && index < len && newContains.test(index)) {
            return selection;
        }

        // Search in both directions with a single pass
        // Iterate until both dec & inc are out of bounds
        int dec = index - 1;
        int inc = index + 1;
        while (dec >= 0 || inc < len) {
            // Check for a lesser neighbor
            if (dec >= 0 && newContains.test(dec)) {
                return oldList.get(dec);
            }

            // Check for a greater neighbor
            if (inc < len && newContains.test(inc)) {
                return oldList.get(inc);
            }

            dec--;
            inc++;
        }

        // No existing entry is still present
        return newList.stream().findFirst().orElse(null);
    }
}
