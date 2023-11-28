package net.xolt.freecam.gui.textures;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.resources.ResourceLocation;

public class NineSliceTexture extends ScaledTexture {
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;

    NineSliceTexture(ResourceLocation identifier, NineSlice scaling) {
        super(identifier, scaling.width(), scaling.height());
        this.left = scaling.border().left();
        this.right = scaling.border().right();
        this.top = scaling.border().top();
        this.bottom = scaling.border().bottom();
    }

    /**
     * Draw the texture to screen, using {@link NineSlice} scaling to fill the specified {@code width}/{@code height}.
     *
     * @param gfx {@link GuiGraphics graphics} context to use
     * @param x x position on screen
     * @param y y position on screen
     * @param width width to draw on screen
     * @param height height to draw on screen
     * @see GuiGraphics#blitNineSlicedSprite(TextureAtlasSprite, NineSlice, int, int, int, int, int) vanilla implementation.
     */
    @Override
    public void draw(GuiGraphics gfx, int x, int y, int width, int height) {
        // No scaling required
        if (width == textureWidth && height == textureHeight) {
            this.drawRegion(gfx, x, y, width, height, 0, 0, textureWidth, textureHeight);
            return;
        }

        int left = Math.min(this.left, width / 2);
        int right = Math.min(this.right, width / 2);
        int top = Math.min(this.top, height / 2);
        int bottom = Math.min(this.bottom, height / 2);

        // Horizontal scaling only
        if (height == textureHeight) {
            // Left
            this.drawRegion(gfx, x, y, left, height, 0, 0, left, textureHeight);
            // Center
            this.drawRegionTiled(gfx, x + left, y, width - right - left, height, left, 0,  textureWidth - right - left, textureHeight);
            // Right
            this.drawRegion(gfx, x + width - right, y, right, height, textureWidth - right, 0, x + width - right, textureHeight);
            return;
        }

        // Vertical scaling only
        if (width == textureWidth) {
            // Top
            this.drawRegion(gfx, x, y, width, top, 0, 0, textureWidth, top);
            // Center
            this.drawRegionTiled(gfx, x, y + top, width, height - bottom - top, 0, top, textureWidth, textureHeight - bottom - top);
            // Bottom
            this.drawRegion(gfx, x, y + height - bottom, width, bottom, 0, textureHeight - bottom, textureWidth, textureHeight - bottom);
            return;
        }

        // Top Left
        this.drawRegion(gfx, x, y, left, top, 0, 0);
        // Top Center
        this.drawRegionTiled(gfx, x + left, y, width - left - right, top, left, 0, textureWidth - left - right, top);
        // Top Right
        this.drawRegion(gfx, x + width - right, y, right, top, textureWidth - right, 0);
        // Bottom Left
        this.drawRegion(gfx, x, y + height - bottom, left, bottom, 0, textureHeight - bottom);
        // Bottom Center
        this.drawRegionTiled(gfx, x + left, y + height - bottom, width - left - right, bottom, left, textureHeight - bottom, textureWidth - left - right, bottom);
        // Bottom Right
        this.drawRegion(gfx, x + width - right, y + height - bottom, right, bottom, textureWidth - right, textureHeight - bottom);
        // Main Left
        this.drawRegionTiled(gfx, x, y + top, left, height - top - bottom, 0, top, left, textureHeight - top - bottom);
        // Main Center
        this.drawRegionTiled(gfx, x + left, y + top, width - left - right, height - top - bottom, left, top, textureWidth - left - right, textureHeight - top - bottom);
        // Main Right
        this.drawRegionTiled(gfx, x + width - right, y + top, right, height - top - bottom, textureWidth - right, top, right, textureHeight - top - bottom);
    }
}
