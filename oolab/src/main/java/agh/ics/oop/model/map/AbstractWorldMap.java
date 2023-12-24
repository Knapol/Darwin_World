package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    protected UUID id = UUID.randomUUID();
    protected HashMap<Vector2d, List<Animal>> animals = new HashMap<>();
    protected List<MapChangeListener> observers = new ArrayList<>();
    protected MapVisualizer mapVisualizer = new MapVisualizer(this);

//    abstract public Boundary getCurrentBounds();

    @Override
    public String toString(){
        Boundary currentBound = getCurrentBounds();
        return mapVisualizer.draw(currentBound.lowerLeft(), currentBound.upperRight());
    }

    public UUID getId(){
        return id;
    }

    @Override
    public void move(Animal animal){
        Vector2d startingPosition = animal.getPosition();
        animal.update(this);

        if (canMoveTo(animal.getPosition())){
            animals.putIfAbsent(animal.getPosition(), new ArrayList<>());
            animals.get(animal.getPosition()).add(animal);
            animals.get(startingPosition).remove(animal);

            if (animals.get(startingPosition).isEmpty()){
                animals.remove(startingPosition);
            }
            mapChanged("Animal moved from position " + startingPosition + " into position " + animal.getPosition());
        }
    }


    @Override
    public void place(Animal animal) throws PositionAlreadyOccupiedException {
        if (!canMoveTo(animal.getPosition())) {
            throw new PositionAlreadyOccupiedException(animal.getPosition());
        }
        animals.putIfAbsent(animal.getPosition(), new ArrayList<>());
        animals.get(animal.getPosition()).add(animal);

        mapChanged("Animal was added into position " + animal.getPosition());
    }

    @Override
    public boolean canMoveTo(Vector2d position){
        // should later change it to sth more cool
        return true;
//        return !animals.containsKey(position);
    }

    @Override
    public boolean isOccupied(Vector2d position){
        return animals.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position){
        if (animals.get(position) != null) {
            return animals.get(position).get(0);
        }
        return null;
    }

    @Override
    public ArrayList<WorldElement> getElements(){
        ArrayList<WorldElement> allAnimals = new ArrayList<>();
        for (List<Animal> setOfAnimals : animals.values()){
            allAnimals.addAll(setOfAnimals);
        }
        return allAnimals;
    }

    public void addObserver(MapChangeListener mapChangeListener){
        observers.add(mapChangeListener);
    }

    public void removeObserver(MapChangeListener mapChangeListener){
        observers.remove(mapChangeListener);
    }

    private void mapChanged(String message){
        for (MapChangeListener mapChangeListener : observers){
            mapChangeListener.mapChanged(this, message);
        }
    }
}
