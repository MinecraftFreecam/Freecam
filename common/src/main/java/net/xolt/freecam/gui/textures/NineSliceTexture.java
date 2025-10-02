package net.xolt.freecam.gui.textures;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.resources.ResourceLocation;

public class NineSliceTexture extends ScaledTexture {
    private final ResourceLocation identifier;
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;

    NineSliceTexture(ResourceLocation identifier, NineSlice scaling) {
        super(identifier, scaling.width(), scaling.height());
        this.identifier = identifier;
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
        // Fast path: no scaling
        if (width == textureWidth && height == textureHeight) {
            blitRegionStretch(gfx, x, y, width, height, 0, 0, textureWidth, textureHeight);
            return;
        }

        // Clamp borders so they never exceed half of the target size
        int l = Math.min(this.left, width / 2);
        int r = Math.min(this.right, width / 2);
        int t = Math.min(this.top, height / 2);
        int b = Math.min(this.bottom, height / 2);

        int midW = Math.max(0, width - l - r);
        int midH = Math.max(0, height - t - b);

        int texMidW = Math.max(0, textureWidth - this.left - this.right);
        int texMidH = Math.max(0, textureHeight - this.top - this.bottom);

        // Corners
        blitRegionStretch(gfx, x, y, l, t, 0, 0, this.left, this.top); // top-left
        blitRegionStretch(gfx, x + width - r, y, r, t, textureWidth - this.right, 0, this.right, this.top); // top-right
        blitRegionStretch(gfx, x, y + height - b, l, b, 0, textureHeight - this.bottom, this.left, this.bottom); // bottom-left
        blitRegionStretch(gfx, x + width - r, y + height - b, r, b, textureWidth - this.right, textureHeight - this.bottom, this.right, this.bottom); // bottom-right

        // Edges
        if (midW > 0) {
            blitRegionStretch(gfx, x + l, y, midW, t, this.left, 0, texMidW, this.top); // top edge
            blitRegionStretch(gfx, x + l, y + height - b, midW, b, this.left, textureHeight - this.bottom, texMidW, this.bottom); // bottom edge
        }

        if (midH > 0) {
            blitRegionStretch(gfx, x, y + t, l, midH, 0, this.top, this.left, texMidH); // left edge
            blitRegionStretch(gfx, x + width - r, y + t, r, midH, textureWidth - this.right, this.top, this.right, texMidH); // right edge
        }

        // Center
        if (midW > 0 && midH > 0) {
            blitRegionStretch(gfx, x + l, y + t, midW, midH, this.left, this.top, texMidW, texMidH);
        }
    }

    /**
     * Stretch-draw a sub-rectangle (u,v,uW,uH) of the texture to (x,y,drawW,drawH).
     */
    private void blitRegionStretch(GuiGraphics gfx, int x, int y, int drawW, int drawH, int u, int v, int uW, int uH) {
        gfx.blit(this.identifier, x, y, drawW, drawH, u, v, uW, uH, this.textureWidth, this.textureHeight);
    }
}
