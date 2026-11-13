package com.jad;

import com.jad.controller.Controller;
import com.jad.controller.IController;
import com.jad.model.ForestType;
import com.jad.model.IForest;
import com.jad.model.Model;
import com.jad.model.RandomMovementStrategy;
import com.jad.view.View;

import java.awt.*;
import java.util.Random;

public enum Main {
    ;

    // Paramètres de la forêt
    private static final ForestType FOREST_TYPE = ForestType.RECTANGULAR;
    private static final int FOREST_WIDTH = 16;
    private static final int FOREST_HEIGHT = 6;
    private static final double TREE_DENSITY = 0.12;

    // Paramètres d'affichage (Step 2 & Step 3)
    private static final int ZOOM = 1;
    private static final int VIEWPORT_WIDTH = 108;
    private static final int VIEWPORT_HEIGHT = 32;

    // Paramètres des enfants (Step 3)
    private static final int CHILD_COUNT = 6;
    private static final int TURN_DELAY_MS = 350;

    public static void main(String[] args) {
        final Random random = new Random();
        final IForest forest = FOREST_TYPE.create(FOREST_WIDTH, FOREST_HEIGHT, TREE_DENSITY, random);

        final Model model = new Model(forest, random);
        model.spawnChildren(CHILD_COUNT, new RandomMovementStrategy(random));

        final View view = new View(model, ZOOM, new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        view.setCenter(FOREST_WIDTH / 2, FOREST_HEIGHT / 2);

        final IController controller = new Controller(model, view, TURN_DELAY_MS);
        controller.proceed();
    }
}
