package agh.ics.oop.model;

import agh.ics.oop.model.map.MoveValidator;
import agh.ics.oop.model.map.WorldMap;

import java.util.ArrayList;
import java.util.Random;

public class Animal implements WorldElement{
    private MapDirection direction;
    private Vector2d position;
    private final int[] genome;
    private int activeGenomeID;
    private int energy = 10;
    private int daysAlive;
    private int numberOfChildren;
    private int numberOfDescendants;
    private WorldMap map;

    public Animal(Vector2d position, WorldMap map){
        this.direction = MapDirection.randomDirection();
        this.position = position;
        this.map = map;
        this.genome = new int[map.getGenomeSize()];
        this.energy = map.getStartingEnergy();
        createGenome(map.getGenomeSize());
    }

    public Animal(Vector2d position, WorldMap map, int[] genome){
        this.direction = MapDirection.randomDirection();
        this.position = position;
        this.map = map;
        this.genome = genome;
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
        this.energy -= 1;
    }

    public Animal breed(Animal other){
        if (this.energy < map.getMinEnergyToBreed() || other.energy < map.getMinEnergyToBreed()){
            return null;
        }

        return new Animal(this.position, this.map, createNewGenome(this.genome, this.energy, other.genome, other.energy));
    }

    private static int[] createNewGenome(int[] genome1, int energy1, int[] genome2,  int energy2){
        int genomeSize = genome1.length;

        if (Math.random() > 0.5){
            int[] tempGenome = genome1;
            genome1 = genome2;
            genome2 = tempGenome;

            int tempEnergy = energy1;
            energy1 = energy2;
            energy2 = tempEnergy;
        }

        int ratio = Math.min(energy1/energy2, energy2/energy1);
        int midPoint = (energy1 >= energy2) ? ratio*genomeSize : genomeSize - genomeSize*ratio;

        int[] newGenome = new int[genomeSize];
        for (int i=0; i<midPoint; i++){
            newGenome[i] = genome1[i];
        }

        for (int i=midPoint; i<genomeSize; i++){
            newGenome[i] = genome2[i];
        }

        return newGenome;
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
