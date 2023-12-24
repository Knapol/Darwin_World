package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import agh.ics.oop.model.map.MoveValidator;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public interface WorldMap extends MoveValidator {
    void place(Animal animal) throws PositionAlreadyOccupiedException;

    void move(Animal animal);

    boolean isOccupied(Vector2d position);

    WorldElement objectAt(Vector2d position);

    ArrayList<WorldElement> getElements();

    UUID getId();

    Boundary getCurrentBounds();
}
