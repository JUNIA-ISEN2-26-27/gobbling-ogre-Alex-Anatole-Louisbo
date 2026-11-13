package com.jad.model;

import java.util.Optional;

public interface IForest {

    int getWidth();

    int getHeight();

    /** Retourne la case en (x, y), ou Optional.empty() si on est hors de la forêt. */
    Optional<Tile> getTile(int x, int y);

    /**
     * Traduit des coordonnées brutes (x, y) en position réelle dans la forêt
     * (en appliquant le bouclage sur les bords magiques), ou Optional.empty() hors limites.
     */
    Optional<Position> resolvePosition(int x, int y);
}
