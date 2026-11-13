package com.jad.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Model implements IModel, IWorldView {

    private final IForest forest;
    private final List<IChild> children;
    private final Set<Position> graves;
    private final Random random;
    private int nextChildId = 1;

    public Model(final IForest forest) {
        this(forest, new Random());
    }

    public Model(final IForest forest, final Random random) {
        this.forest = Objects.requireNonNull(forest, "forest ne peut pas être null");
        this.random = Objects.requireNonNull(random, "random ne peut pas être null");
        this.children = new ArrayList<>();
        this.graves = new HashSet<>();
    }

    @Override
    public IForest getForest() {
        return this.forest;
    }

    @Override
    public List<IChild> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public Set<Position> getGraves() {
        return Collections.unmodifiableSet(this.graves);
    }

    public void addChild(final IChild child) {
        this.children.add(Objects.requireNonNull(child, "child ne peut pas être null"));
    }

    /**
     * Place aléatoirement {@code count} enfants sur des cases libres et traversables de la forêt.
     */
    public void spawnChildren(final int count, final IMovementStrategy strategy) {
        if (count < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants ne peut pas être négatif");
        }
        final List<Position> freePositions = this.findFreePositions();
        if (freePositions.size() < count) {
            throw new IllegalStateException("Pas assez de cases libres dans la forêt pour placer " + count + " enfants");
        }
        Collections.shuffle(freePositions, this.random);
        for (int i = 0; i < count; i++) {
            this.children.add(new Child(this.nextChildId++, freePositions.get(i), strategy));
        }
    }

    @Override
    public boolean isBlockedForChild(final Position position) {
        final Optional<Tile> tile = this.forest.getTile(position.x(), position.y());
        if (tile.isEmpty() || !tile.get().isTraversable()) {
            return true;
        }
        if (this.graves.contains(position)) {
            return true;
        }
        for (final IChild other : this.children) {
            if (other.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void nextTurn() {
        for (final IChild child : this.children) {
            final Direction proposed = child.chooseDirection(this);
            if (proposed == null || proposed == Direction.NONE) {
                continue;
            }
            final Position rawTarget = proposed.apply(child.getPosition());
            final Optional<Position> resolvedTarget = this.forest.resolvePosition(rawTarget.x(), rawTarget.y());
            if (resolvedTarget.isPresent() && !this.isBlockedForChild(resolvedTarget.get())) {
                child.setPosition(resolvedTarget.get());
                child.setLastDirection(proposed);
            }
        }
    }

    private List<Position> findFreePositions() {
        final List<Position> free = new ArrayList<>();
        for (int y = 0; y < this.forest.getHeight(); y++) {
            for (int x = 0; x < this.forest.getWidth(); x++) {
                final Position pos = new Position(x, y);
                if (!this.isBlockedForChild(pos)) {
                    free.add(pos);
                }
            }
        }
        return free;
    }
}
