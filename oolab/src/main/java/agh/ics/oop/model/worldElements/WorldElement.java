package agh.ics.oop.model.worldElements;

import agh.ics.oop.model.Vector2d;

public interface WorldElement {
    boolean isAt(Vector2d position);

    Vector2d getPosition();
}
