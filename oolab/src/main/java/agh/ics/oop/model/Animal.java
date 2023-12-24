package agh.ics.oop.model;

import agh.ics.oop.model.map.MoveValidator;

import java.util.ArrayList;
import java.util.Random;

public class Animal implements WorldElement{
    private MapDirection direction;
    private Vector2d position;
    private final int[] genome;
    private int activeGenomeID;

    private int energy;
    private int daysAlive;

    private int numberOfChildren;
    private int numberOfDescendants;

    public Animal(Vector2d position, int genomeSize){
        this.direction = MapDirection.randomDirection();
        this.position = position;
        genome = new int[genomeSize];
        createGenome(genomeSize);
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

    private void createGenome(int genomeSize){
        Random randomGenerator = new Random();
        for (int i=0; i<genomeSize; i++){
            genome[i] = randomGenerator.nextInt(8); //maybe file for all static
        }
    }

    public MapDirection getDirection(){
        return direction;
    }

    public Vector2d getPosition(){
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public int getDaysAlive(){
        return daysAlive;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public int getNumberOfDescendants() {
        return numberOfDescendants;
    }
}
