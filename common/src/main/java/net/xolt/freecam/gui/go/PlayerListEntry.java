package net.xolt.freecam.gui.go;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import net.xolt.freecam.util.FreecamPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static net.xolt.freecam.Freecam.MC;
import static net.xolt.freecam.gui.go.GotoScreen.WHITE_COLOR;

public class PlayerListEntry extends TargetListEntry {

    private final Component displayText;
    private final AbstractClientPlayer player;
    private final @Nullable Supplier<PlayerSkin> skinSupplier;

    public PlayerListEntry(TargetListWidget widget, AbstractClientPlayer player) {
        super(MC, widget);
        this.player = player;

        var text = player.getName().plainCopy();
        if (Objects.equals(player, MC.player)) {
            text = Component.translatable("gui.freecam.goto.entry.player.you", text);
        }
        this.displayText = text;

        var playerInfo = MC.player.connection.getPlayerInfo(player.getUUID());
        this.skinSupplier = playerInfo == null ? null : playerInfo::getSkin;
    }

    @Override
    public void render(GuiGraphics gfx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.render(gfx, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

        int padding = 4;
        int skinSize = 24;
        boolean hasSkin = skinSupplier != null;

        if (hasSkin) {
            int skinX = x + padding;
            int skinY = y + (entryHeight - skinSize) / 2;
            PlayerFaceRenderer.draw(gfx, skinSupplier.get(), skinX, skinY, skinSize);
        }

        int textX = x + padding + (hasSkin ? skinSize + padding : 0);
        int textY = y + (entryHeight - mc.font.lineHeight) / 2;
        gfx.drawString(mc.font, displayText, textX, textY, WHITE_COLOR, false);
    }

    @Override
    public int compareTo(@NotNull TargetListEntry entry) {
        // Sort before non-player entries
        if (!(entry instanceof PlayerListEntry playerEntry)) {
            return -1;
        }

        // Sort mc.player before other players
        if (Objects.equals(getUUID(), playerEntry.getUUID())) {
            return 0;
        }
        if (Objects.equals(getUUID(), mc.player.getUUID())) {
            return -1;
        }
        if (Objects.equals(playerEntry.getUUID(), mc.player.getUUID())) {
            return 1;
        }

        // Fallback to default comparison
        return super.compareTo(entry);
    }

    @Override
    public String getName() {
        return player.getScoreboardName();
    }

    @Override
    public void go() {
        mc.setScreen(null);
        Freecam.gotoPosition(FreecamPosition.of(player, ModConfig.get().hidden.gotoPlayerPerspective));
    }

    public UUID getUUID() {
        return player.getUUID();
    }
}
