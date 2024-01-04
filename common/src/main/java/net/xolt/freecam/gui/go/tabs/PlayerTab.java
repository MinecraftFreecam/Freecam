package net.xolt.freecam.gui.go.tabs;

import com.google.common.base.Suppliers;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.gui.go.PlayerListEntry;
import net.xolt.freecam.gui.go.TargetListEntry;
import net.xolt.freecam.gui.go.TargetListWidget;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.variant.api.BuildVariant;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.xolt.freecam.Freecam.MC;

class PlayerTab implements Tab {

    private final Supplier<CycleButton<ModConfig.Perspective>> perspectiveButton = Suppliers.memoize(() -> CycleButton
            .builder((ModConfig.Perspective value) -> Component.translatable(value.getKey()))
            .withValues(ModConfig.Perspective.values())
            .withInitialValue(ModConfig.get().hidden.gotoPlayerPerspective)
            .withTooltip(value -> Tooltip.create(Component.translatable("gui.freecam.goto.button.perspective.@Tooltip")))
            .displayOnlyValue()
            .create(0, 0, 80, 20, Component.empty(), (button, value) -> {
                ModConfig.get().hidden.gotoPlayerPerspective = value;
                ModConfig.save();
            }));

    @Override
    public List<TargetListEntry> provideEntriesFor(TargetListWidget widget) {
        // Store the existing entries in a UUID map for easy lookup
        Map<UUID, PlayerListEntry> currentEntries = widget.children()
                .parallelStream()
                .filter(PlayerListEntry.class::isInstance)
                .map(PlayerListEntry.class::cast)
                .collect(Collectors.toUnmodifiableMap(PlayerListEntry::getUUID, Function.identity()));

        // Map the in-range players into PlayerListEntries
        // Use existing entries if possible
        return MC.level.players()
                .parallelStream()
                .filter(player -> !(player instanceof FreeCamera))
                .filter(this::permitted)
                .map(player -> Objects.requireNonNullElseGet(
                        currentEntries.get(player.getUUID()),
                        () -> new PlayerListEntry(widget, player)))
                .map(TargetListEntry.class::cast)
                .toList();
    }

    @Override
    public List<AbstractButton> extraButtons() {
        return Collections.singletonList(perspectiveButton.get());
    }

    private boolean permitted(AbstractClientPlayer player) {
        // TODO check if player is visible
        return BuildVariant.getInstance().cheatsPermitted() || Objects.equals(MC.player, player);
    }
}
