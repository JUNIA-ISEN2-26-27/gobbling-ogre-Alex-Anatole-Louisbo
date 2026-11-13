package com.jad;

import com.jad.controller.Controller;
import com.jad.controller.IController;
import com.jad.model.ForestType;
import com.jad.model.IForest;
import com.jad.model.Model;
import com.jad.view.View;

import java.awt.*;
import java.util.Random;

public enum Main {
    ;

    // Paramètres de la forêt
    private static final ForestType FOREST_TYPE = ForestType.HORIZONTALLY_MAGIC;
    private static final int FOREST_WIDTH = 40;
    private static final int FOREST_HEIGHT = 10;
    private static final double TREE_DENSITY = 0.1;

    // Paramètres d'affichage (Step 2)
    private static final int ZOOM = 3; // 1 = petit (3x2), 2 = moyen (6x4), 3 = grand (9x6)
    private static final int VIEWPORT_WIDTH = 90;
    private static final int VIEWPORT_HEIGHT = 24;

    public static void main(String[] args) {
        final IForest forest = FOREST_TYPE.create(FOREST_WIDTH, FOREST_HEIGHT, TREE_DENSITY, new Random());

        final Model model = new Model(forest);
        final View view = new View(model, ZOOM, new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        view.setCenter(FOREST_WIDTH / 2, FOREST_HEIGHT / 2);

        final IController controller = new Controller(model, view);
        controller.proceed();
    }
}
