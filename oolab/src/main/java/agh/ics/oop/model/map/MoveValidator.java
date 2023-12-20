package agh.ics.oop.model.map;

import agh.ics.oop.model.Vector2d;

public interface MoveValidator {
    boolean canMoveTo(Vector2d position);
}
