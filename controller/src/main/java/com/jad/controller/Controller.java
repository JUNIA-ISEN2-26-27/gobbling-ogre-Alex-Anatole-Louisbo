package com.jad.controller;

import com.jad.model.IModel;
import com.jad.view.IView;

public class Controller implements IController {
    private static final int DEFAULT_TURN_DELAY_MS = 350;

    private final IModel model;
    private final IView view;
    private final int turnDelayMs;

    public Controller(final IModel model, final IView view) {
        this(model, view, DEFAULT_TURN_DELAY_MS);
    }

    public Controller(final IModel model, final IView view, final int turnDelayMs) {
        this.model = model;
        this.view = view;
        this.turnDelayMs = Math.max(10, turnDelayMs);
        this.view.setController(this);
    }

    @Override
    public void proceed() {
        while (!Thread.currentThread().isInterrupted()) {
            this.view.refresh();
            try {
                Thread.sleep(this.turnDelayMs);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            this.model.nextTurn();
        }
    }
}
