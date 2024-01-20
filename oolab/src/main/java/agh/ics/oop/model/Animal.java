package agh.ics.oop.model;

import agh.ics.oop.model.map.WorldMap;

import java.util.HashSet;
import java.util.Set;

public class Animal implements WorldElement{
    private final long ID;
    private MapDirection direction;
    private Vector2d position;
    private final Genome genome;
    private int energy;
    private int daysAlive;
    private int eatenGrass;
    private int deathDay;
    private Animal father;
    private Animal mother;
    private int numberOfChildren;
    private int numberOfDescendants;
    private final WorldMap map;

    public Animal(Vector2d position, WorldMap map){
        this.direction = MapDirection.randomDirection();
        this.position = position;
        this.map = map;
        this.genome = new Genome(map.getGenomeSize());
        this.energy = map.getStartingEnergy();
        this.daysAlive = 0;
        this.ID = map.getNextAnimalID();
    }

    public Animal(Vector2d position, WorldMap map, Genome genome, int energy, Animal father, Animal mother){
        this.direction = MapDirection.randomDirection();
        this.position = position;
        this.map = map;
        this.genome = genome;
        this.energy = energy;
        this.father = father;
        this.mother = mother;
        this.daysAlive = 0;
        this.ID = map.getNextAnimalID();
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

    public void update(){
        rotate();
        move();
        daysAlive++;
    }

    public void die(int day){
        deathDay = day;
    }

    private void rotate(){
        direction = direction.rotate(genome.getNextGene(map.getAnimalBehavior()));
    }

    private void move(){
        this.energy -= map.getMoveCost();
        Vector2d testPosition;

        testPosition = position.add(direction.toUnitVector());

        if (testPosition.getY() < map.getCurrentBounds().lowerLeft().getY() || testPosition.getY() > map.getCurrentBounds().upperRight().getY()){
            this.direction = direction.rotate(4);
            return;
        }

        if (testPosition.getX() < map.getCurrentBounds().lowerLeft().getX()){
            // it means that our animal need to teleport to other side of a map
            testPosition = new Vector2d(map.getCurrentBounds().upperRight().getX(), testPosition.getY());
            position = testPosition;
            return;
        }

        if (testPosition.getX() > map.getCurrentBounds().upperRight().getX()){
            testPosition = new Vector2d(map.getCurrentBounds().lowerLeft().getX(), testPosition.getY());
            position = testPosition;
            return;
        }
        position = testPosition;
    }

    private void dfsVisit(Animal animal, Set<Animal> visited){
        animal.numberOfDescendants++;
        visited.add(animal);

        if (!visited.contains(animal.mother) && animal.mother != null) {
            dfsVisit(animal.mother, visited);
        }

        if (!visited.contains(animal.father) && animal.father != null){
            dfsVisit(animal.father, visited);
        }
    }

    private void updateDescendants(Animal other){
        Set<Animal> visited = new HashSet<>();

        dfsVisit(this, visited);

        if (!visited.contains(other)) {
            dfsVisit(other, visited);
        }
    }

    public Animal breed(Animal other){
        if (this.energy < map.getMinEnergyToBreed() || other.energy < map.getMinEnergyToBreed()){
            return null;
        }

        Genome childGenome = (this.energy >= other.energy) ?
                this.genome.createNewGenome(other.genome, this.energy, other.energy) :
                other.genome.createNewGenome(this.genome, other.energy, this.energy);

        childGenome.mutate(this.map.getMinMutations(), this.map.getMaxMutations());

        this.energy -= map.getEnergyUseForBreeding();
        other.energy -= map.getEnergyUseForBreeding();

        updateDescendants(other);

        this.numberOfChildren++;
        other.numberOfChildren++;

        int childEnergy = 2 * map.getMinEnergyToBreed();

        return new Animal(this.position, this.map, childGenome, childEnergy, this, other);
    }

    public void eat(int grassEnergy){
        this.energy += grassEnergy;
        this.eatenGrass += 1;
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

    public long getID(){
        return ID;
    }

    public Genome getGenome(){
        return genome;
    }

    public int getEatenGrass(){
        return eatenGrass;
    }

    public int getDeathDay(){
        return deathDay;
    }
}
