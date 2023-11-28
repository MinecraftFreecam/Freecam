package net.xolt.freecam.gui.textures;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;

public class TileTexture extends ScaledTexture {

    TileTexture(ResourceLocation identifier, GuiSpriteScaling.Tile scaling) {
        super(identifier, scaling.width(), scaling.height());
    }

    /**
     * Draw the texture to screen, tiling as necessary to fill the specified {@code width}/{@code height}.
     *
     * @param gfx {@link GuiGraphics graphics} context to use
     * @param x x position on screen
     * @param y y position on screen
     * @param width width to draw on screen
     * @param height height to draw on screen
     * @see GuiGraphics#blitTiledSprite(TextureAtlasSprite, int, int, int, int, int, int, int, int, int, int, int) vanilla implementation
     */
    @Override
    public void draw(GuiGraphics gfx, int x, int y, int width, int height) {
        if (width == textureWidth && height == textureHeight) {
            // Draw without scaling
            this.drawRegion(gfx, x, y, width, height, 0, 0, textureWidth, textureHeight);
            return;
        }

        this.drawRegionTiled(gfx, x, y, width, height, 0, 0,  textureWidth, textureHeight);
    }
}
