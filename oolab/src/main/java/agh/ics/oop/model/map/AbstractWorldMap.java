package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

public abstract class AbstractWorldMap implements WorldMap {
    protected UUID id = UUID.randomUUID();
    protected Map<Vector2d, Animal> animals = new HashMap<>();

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
    public void update(){
        for (Animal animal : animals.values()){
            Vector2d startingPosition = animal.getPosition();
            animal.update(this);

            if (canMoveTo(animal.getPosition())){
                animals.put(animal.getPosition(), animal);
                animals.remove(startingPosition);
                mapChanged("Animal moved from position " + startingPosition + " into position " + animal.getPosition());
            }
            else{
                mapChanged("Nie pyklo" + animal.getDirection());
            }
        }
    }

    @Override
    public void place(Animal animal) throws PositionAlreadyOccupiedException {
        if (!canMoveTo(animal.getPosition())) {
            throw new PositionAlreadyOccupiedException(animal.getPosition());
        }
        animals.put(animal.getPosition(), animal);
        mapChanged("Animal was added into position " + animal.getPosition());
    }

    @Override
    public boolean canMoveTo(Vector2d position){
        return !animals.containsKey(position);
    }

    @Override
    public boolean isOccupied(Vector2d position){
        return animals.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position){
        return animals.get(position);
    }

    @Override
    public ArrayList<WorldElement> getElements(){
        return new ArrayList<>(animals.values());
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
