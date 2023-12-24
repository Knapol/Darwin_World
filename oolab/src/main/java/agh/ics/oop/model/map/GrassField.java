package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.map.AbstractWorldMap;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class GrassField extends AbstractWorldMap {
    private final Map<Vector2d, Grass> grasses;

    public GrassField(int numberOfGrass){
        this.grasses = new HashMap<>();

        RandomPositionGenerator randomPositionGenerator = new RandomPositionGenerator(
                (int)Math.sqrt(10*numberOfGrass),
                (int)Math.sqrt(10*numberOfGrass),
                numberOfGrass
        );

        for(Vector2d grassPosition : randomPositionGenerator) {
            grasses.put(grassPosition, new Grass(grassPosition));
        }
    }

    @Override
    public Boundary getCurrentBounds(){
        Vector2d max = null;
        Vector2d min = null;

        for (WorldElement element: getElements()){
            if (max == null || min == null){
                max = element.getPosition();
                min = element.getPosition();
            }
            else {
                max = max.upperRight(element.getPosition());
                min = min.lowerLeft(element.getPosition());
            }
        }
        return new Boundary(min, max);
    }

    @Override
    public boolean isOccupied(Vector2d position){
        return super.isOccupied(position) || grasses.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position){
        WorldElement object = super.objectAt(position);

        if (object != null){
            return object;
        }
        return grasses.get(position);
    }

//    @Override
//    public ArrayList<WorldElement> getElements(){
//        ArrayList<WorldElement> updatedMap = super.getElements();
//        updatedMap.addAll(grasses.values());
//        return updatedMap;
//    }
}
