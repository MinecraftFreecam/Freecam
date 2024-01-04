package net.xolt.freecam.gui.textures;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Reimplement a subset of vanilla Minecraft's {@link TextureAtlasSprite atlas/sprite} rendering so that we can draw
 * scaled textures without needing to use {@link GuiSpriteManager}.
 *
 * @see GuiSpriteScaling
 * @see TextureAtlasSprite
 */
public abstract class ScaledTexture {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected final ResourceLocation location;
    protected final int textureWidth;
    protected final int textureHeight;

    protected ScaledTexture(ResourceLocation location, int width, int height) {
        this.location = location;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    /**
     * Get the {@code ScaledTexture} representing the specified texture. The texture must have a {@code mcmeta} file
     * containing a {@link GuiMetadataSection} specifying a supported {@link GuiSpriteScaling}.
     * <p>
     * Currently, {@link GuiSpriteScaling.Tile tile} and {@link GuiSpriteScaling.NineSlice nine_slice} are supported.
     * Notably, {@link GuiSpriteScaling.Stretch stretch} is not.
     *
     * @param location the location of the texture asset
     * @return a {@code ScaledTexture} representing the asset
     * @throws UnsupportedOperationException if a supported {@link GuiSpriteScaling scaling type} is not specified
     */
    public static ScaledTexture get(ResourceLocation location) {
        GuiSpriteScaling scaling = Minecraft.getInstance()
                .getResourceManager()
                .getResource(location)
                .map(resource -> {
                    try {
                        return resource.metadata();
                    } catch (IOException e) {
                        LOGGER.error("Unable to parse metadata from {}", location, e);
                        return null;
                    }
                })
                .flatMap(metadata -> metadata.getSection(GuiMetadataSection.TYPE))
                .orElse(GuiMetadataSection.DEFAULT)
                .scaling();

        return switch (scaling.type()) {
            case TILE -> new TileTexture(location, (GuiSpriteScaling.Tile) scaling);
            case NINE_SLICE -> new NineSliceTexture(location, (GuiSpriteScaling.NineSlice) scaling);
            default -> throw new UnsupportedOperationException("Unsupported scaling type: " + scaling.type());
        };
    }

    /**
     * Draw the texture to screen, scaling to the specified {@code width}/{@code height} using the specific implementation.
     *
     * @param gfx {@link GuiGraphics graphics} context to use
     * @param x x position on screen
     * @param y y position on screen
     * @param width width to draw on screen
     * @param height height to draw on screen
     */
    public abstract void draw(GuiGraphics gfx, int x, int y, int width, int height);

    /**
     * Draw a region of the texture to screen, without scaling.
     *
     * @param gfx {@link GuiGraphics graphics} context to use
     * @param x x position on screen
     * @param y y position on screen
     * @param width width of the texture region
     * @param height height of the texture region
     * @param u left position in texture
     * @param v top position in texture
     */
    protected void drawRegion(GuiGraphics gfx, int x, int y, int width, int height, int u, int v) {
        this.drawRegion(gfx, x, y, width, height, u, v, width, height);
    }

    /**
     * Draw a region of the texture to screen, scaling to the {@code width}/{@code height} drawn to screen.
     *
     * @param gfx {@link GuiGraphics graphics} context to use
     * @param x x position on screen
     * @param y y position on screen
     * @param width width to draw on screen
     * @param height height to draw on screen
     * @param u left position in texture
     * @param v top position in texture
     * @param regionWidth width of the texture region
     * @param regionHeight height of the texture region
     * @see GuiGraphics#blit(net.minecraft.resources.ResourceLocation, int, int, int, int, float, float, int, int, int, int) vanilla implementation
     */
    protected void drawRegion(GuiGraphics gfx, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {
        gfx.blit(location, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    /**
     * Draw a region of the texture to screen, tiling as necessary to fill the {@code width}/{@code height} drawn to screen.
     *
     * @param gfx {@link GuiGraphics graphics} context to use
     * @param x x position on screen
     * @param y y position on screen
     * @param width width to draw on screen
     * @param height height to draw on screen
     * @param u left position in texture
     * @param v top position in texture
     * @param regionWidth width of the texture region
     * @param regionHeight height of the texture region
     * @see GuiGraphics#blitTiledSprite(TextureAtlasSprite, int, int, int, int, int, int, int, int, int, int, int) vanilla implementation
     */
    protected void drawRegionTiled(GuiGraphics gfx, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {
        if (width <= 0 || height <= 0) {
            // Nothing to do
            return;
        }

        if (regionWidth <= 0 || regionHeight <= 0) {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + regionWidth + "x" + regionHeight);
        }

        // `progress` tracks how many pixels have been rendered on each axis so far.
        // `next` is the number of pixels to draw during this iteration, clamped to avoid overflowing width or height.
        for (int xProgress = 0; xProgress < width; xProgress += regionWidth) {
            int xNext = Math.min(regionWidth, width - xProgress);
            for (int yProgress = 0; yProgress < height; yProgress += regionHeight) {
                int yNext = Math.min(regionHeight, height - yProgress);
                drawRegion(gfx, x + xProgress, y + yProgress, xNext, yNext, u, v, regionWidth, regionHeight);
            }
        }
    }
}
