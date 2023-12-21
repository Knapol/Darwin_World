package agh.ics.oop.model;

import agh.ics.oop.model.map.MoveValidator;

public class Animal implements WorldElement{
    private MapDirection direction;
    private Vector2d position;
    private final int[] genome = {0, 1, 5, 6, 7, 6};
    private int activeGenomeID;
    private int energy;
    private int daysAlive;

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
            case NORTHEAST -> "NE";
            case EAST -> "E";
            case SOUTHEAST -> "SE";
            case SOUTH -> "S";
            case SOUTHWEST -> "SW";
            case WEST -> "W";
            case NORTHWEST -> "NW";
        };
    }

    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void update(MoveValidator moveValidator){
        rotate();
        move(moveValidator);
    }

    private void rotate(){
        direction = direction.rotate(genome[activeGenomeID]);
        activeGenomeID++;
        if (activeGenomeID >= genome.length){
            activeGenomeID = 0;
        }
    }

    private void move(MoveValidator moveValidator){
        Vector2d testPosition;

        testPosition = position.add(direction.toUnitVector());

        if (moveValidator.canMoveTo(testPosition)){
            position = testPosition;
        }
    }

    public MapDirection getDirection(){
        return direction;
    }

    public Vector2d getPosition(){
        return position;
    }
}
