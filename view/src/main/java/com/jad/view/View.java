package com.jad.view;

import com.jad.controller.IController;
import com.jad.model.IForest;
import com.jad.model.Model;
import com.jad.model.Tile;
import com.jad.textwindow.TextWindow;
import com.jad.textwindow.TextWindowSettings;

import java.awt.*;
import java.util.Optional;

public class View implements IView {
    private static final String TITLE = "Ogre Boulotteur";
    private static final int DEFAULT_ZOOM = 2;
    private static final int DEFAULT_VIEWPORT_WIDTH = 90;
    private static final int DEFAULT_VIEWPORT_HEIGHT = 24;

    private final Model model;
    private final Dimension viewportDimension;
    private IController controller;
    private TextWindow window;
    private TileSpriteSet spriteSet;
    private Integer centerX;
    private Integer centerY;

    public View(final Model model) {
        this(model, DEFAULT_ZOOM, new Dimension(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT));
    }

    public View(final Model model, final int zoom) {
        this(model, zoom, new Dimension(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT));
    }

    public View(final Model model, final int zoom, final Dimension viewportDimension) {
        this.model = model;
        this.spriteSet = new TileSpriteSet(zoom);
        this.viewportDimension = new Dimension(viewportDimension);
    }

    @Override
    public void setController(final IController controller) {
        this.controller = controller;
    }

    public int getZoom() {
        return this.spriteSet.getZoom();
    }

    public void setZoom(final int zoom) {
        this.spriteSet = new TileSpriteSet(zoom);
    }

    public void setCenter(final int x, final int y) {
        this.centerX = x;
        this.centerY = y;
    }

    @Override
    public void refresh() {
        final IForest forest = this.model.getForest();
        if (this.centerX == null || this.centerY == null) {
            this.centerX = forest.getWidth() / 2;
            this.centerY = forest.getHeight() / 2;
        }
        if (this.window == null) {
            this.window = this.createWindow();
        }
        this.window.display(this.render(forest));
    }

    private String render(final IForest forest) {
        final int spriteWidth = this.spriteSet.getSpriteWidth();
        final int spriteHeight = this.spriteSet.getSpriteHeight();

        final int visibleTilesX = Math.max(1, this.viewportDimension.width / spriteWidth);
        final int visibleTilesY = Math.max(1, this.viewportDimension.height / spriteHeight);

        final int startX = this.centerX - (visibleTilesX / 2);
        final int startY = this.centerY - (visibleTilesY / 2);

        final StringBuilder builder = new StringBuilder();
        for (int tileRow = 0; tileRow < visibleTilesY; tileRow++) {
            final int worldY = startY + tileRow;
            for (int spriteRow = 0; spriteRow < spriteHeight; spriteRow++) {
                for (int tileCol = 0; tileCol < visibleTilesX; tileCol++) {
                    final int worldX = startX + tileCol;
                    final Optional<Tile> tile = forest.getTile(worldX, worldY);
                    builder.append(this.spriteSet.getSpriteLine(tile, spriteRow));
                }
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private TextWindow createWindow() {
        final TextWindowSettings settings = new TextWindowSettings();
        settings.setTitle(TITLE);
        settings.setScreenDimension(this.viewportDimension);
        final TextWindow textWindow = new TextWindow(settings);
        textWindow.setVisible(true);
        return textWindow;
    }
}
