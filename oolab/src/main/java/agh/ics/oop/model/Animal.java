package agh.ics.oop.model;

import agh.ics.oop.model.map.MoveValidator;

public class Animal implements WorldElement{
    private MapDirection direction;
    private Vector2d position;

    public Animal(Vector2d position){
        this.direction = MapDirection.NORTH;
        this.position = position;
    }
    public Animal(){
        this(new Vector2d(2,2));
    }

    @Override
    public String toString(){
        return switch(direction){
            case NORTH -> "N";
            case EAST -> "E";
            case SOUTH -> "S";
            case WEST -> "W";
        };
    }

    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void move(MoveDirection moveDirection, MoveValidator moveValidator){
        Vector2d testPosition;

        switch (moveDirection){
            case LEFT -> direction = direction.previous();
            case RIGHT -> direction = direction.next();
            case FORWARD -> {
                testPosition = position.add(direction.toUnitVector());
                if (moveValidator.canMoveTo(testPosition)){
                    position = testPosition;
                }
            }
            case BACKWARD -> {
                testPosition = position.subtract(direction.toUnitVector());
                if (moveValidator.canMoveTo(testPosition)){
                    position = testPosition;
                }
            }
        }
    }

    public MapDirection getDirection(){
        return direction;
    }

    public Vector2d getPosition(){
        return position;
    }
}
