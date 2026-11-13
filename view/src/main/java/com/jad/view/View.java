package com.jad.view;

import com.jad.controller.IController;
import com.jad.model.IChild;
import com.jad.model.IForest;
import com.jad.model.Model;
import com.jad.model.Position;
import com.jad.model.Tile;
import com.jad.textwindow.TextWindow;
import com.jad.textwindow.TextWindowSettings;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class View implements IView {
    private static final String TITLE = "Ogre Boulotteur";
    private static final int DEFAULT_ZOOM = 2;
    private static final int DEFAULT_VIEWPORT_WIDTH = 108;
    private static final int DEFAULT_VIEWPORT_HEIGHT = 32;
    private static final float BASE_FONT_SIZE = 14f;

    private final Model model;
    private final Dimension viewportDimension;
    private final TileSpriteSet spriteSet;
    private IController controller;
    private TextWindow window;
    private int zoom;
    private Integer centerX;
    private Integer centerY;
    private IChild followedChild;

    public View(final Model model) {
        this(model, DEFAULT_ZOOM, new Dimension(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT));
    }

    public View(final Model model, final int zoom) {
        this(model, zoom, new Dimension(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT));
    }

    public View(final Model model, final int zoom, final Dimension viewportDimension) {
        if (zoom < 1) {
            throw new IllegalArgumentException("Le zoom doit être supérieur ou égal à 1");
        }
        this.model = model;
        this.zoom = zoom;
        this.spriteSet = new TileSpriteSet();
        this.viewportDimension = new Dimension(viewportDimension);
    }

    @Override
    public void setController(final IController controller) {
        this.controller = controller;
    }

    public int getZoom() {
        return this.zoom;
    }

    public void setZoom(final int zoom) {
        if (zoom < 1) {
            throw new IllegalArgumentException("Le zoom doit être supérieur ou égal à 1");
        }
        this.zoom = zoom;
    }

    public void setCenter(final int x, final int y) {
        this.followedChild = null;
        this.centerX = x;
        this.centerY = y;
    }

    /** Sélectionne un enfant à suivre au centre de la zone affichée. */
    public void followChild(final IChild child) {
        this.followedChild = child;
    }

    /** Sélectionne l'enfant d'indice {@code index} à suivre au centre de la zone affichée. */
    public void followChildByIndex(final int index) {
        final List<IChild> children = this.model.getChildren();
        if (index >= 0 && index < children.size()) {
            this.followedChild = children.get(index);
        }
    }

    @Override
    public void refresh() {
        final IForest forest = this.model.getForest();
        if (this.followedChild != null) {
            final Position pos = this.followedChild.getPosition();
            this.centerX = pos.x();
            this.centerY = pos.y();
        } else if (this.centerX == null || this.centerY == null) {
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

        final int visibleTilesX = Math.max(1, (this.viewportDimension.width / this.zoom) / spriteWidth);
        final int visibleTilesY = Math.max(1, (this.viewportDimension.height / this.zoom) / spriteHeight);

        final int startX = this.centerX - (visibleTilesX / 2);
        final int startY = this.centerY - (visibleTilesY / 2);

        final Map<Position, IChild> childByPosition = new HashMap<>();
        for (final IChild child : this.model.getChildren()) {
            childByPosition.put(child.getPosition(), child);
        }

        final StringBuilder builder = new StringBuilder();
        for (int tileRow = 0; tileRow < visibleTilesY; tileRow++) {
            final int worldY = startY + tileRow;
            for (int spriteRow = 0; spriteRow < spriteHeight; spriteRow++) {
                for (int tileCol = 0; tileCol < visibleTilesX; tileCol++) {
                    final int worldX = startX + tileCol;
                    final Optional<Position> resolvedPos = forest.resolvePosition(worldX, worldY);
                    final IChild child = resolvedPos.map(childByPosition::get).orElse(null);

                    if (child != null) {
                        builder.append(this.spriteSet.getChildSpriteLine(spriteRow));
                    } else {
                        final Optional<Tile> tile = forest.getTile(worldX, worldY);
                        builder.append(this.spriteSet.getSpriteLine(tile, spriteRow));
                    }
                }
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private TextWindow createWindow() {
        final TextWindowSettings settings = new TextWindowSettings();
        settings.setTitle(TITLE);
        settings.setFontSize(BASE_FONT_SIZE * this.zoom);
        settings.setScreenDimension(new Dimension(
                Math.max(1, this.viewportDimension.width / this.zoom),
                Math.max(1, this.viewportDimension.height / this.zoom)
        ));
        final TextWindow textWindow = new TextWindow(settings);
        textWindow.setVisible(true);
        return textWindow;
    }
}
