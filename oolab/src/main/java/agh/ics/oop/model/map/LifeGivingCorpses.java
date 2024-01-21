package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.RandomLGCPositionGenerator;
import agh.ics.oop.model.worldElements.Animal;
import agh.ics.oop.model.worldElements.Grass;
import agh.ics.oop.model.worldElements.WorldElement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LifeGivingCorpses extends AbstractWorldMap {
    private final RandomLGCPositionGenerator randomPositionGenerator;
    private final int betterFieldDuration;
    private HashMap<Vector2d, Integer> betterFields;

    public LifeGivingCorpses(Settings settings){
        super(settings);
        this.betterFieldDuration = settings.betterFieldDuration();
        this.randomPositionGenerator = new RandomLGCPositionGenerator(settings.mapWidth(), settings.mapHeight(), settings.grassCount());

        this.betterFields = new HashMap<>();

        for(Vector2d grassPosition : randomPositionGenerator) {
            grasses.put(grassPosition, new Grass(grassPosition));
        }
    }

    @Override
    public boolean isOccupied(Vector2d position){
        return super.isOccupied(position) || grasses.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position){
        WorldElement object = super.objectAt(position);
        if (object == null){
            if (grasses.get(position) != null) {
                return grasses.get(position);
            }
        }
        return object;
    }

    //when animal ate grass
    @Override
    protected void updateRandomPositionGenerator(Vector2d pos){
        if (betterFields.containsKey(pos)){
            //the grass was on the map, so it wasnt in generator
            randomPositionGenerator.addNearCorpsesPosition(pos, false);
        }
        else{
            randomPositionGenerator.addNormalEmptyPosition(pos, false);
        }
    }

    @Override
    public void handleAnimalDeath(Animal animal){
        super.handleAnimalDeath(animal);
        addNewBetterFields(animal.getPosition());
    }

    private void addNewBetterFields(Vector2d pos){
        Boundary boundary = getCurrentBounds();

        //the field on which animal died
        betterFields.put(pos, betterFieldDuration);
        addFieldToGeneratorAfterDeath(pos);

        //searching in all directions around this field to check if there is a potential betterField
        for(MapDirection mapDirection : MapDirection.values()){
            Vector2d potentialField = pos.add(mapDirection.toUnitVector());

            if (potentialField.getY() < boundary.lowerLeft().getY() || potentialField.getY() > boundary.upperRight().getY()){
                continue;
            }

            if (potentialField.getX() < boundary.lowerLeft().getX()){
                Vector2d actualPosition = new Vector2d(boundary.upperRight().getX(), potentialField.getY());
                betterFields.put(actualPosition, betterFieldDuration);
                addFieldToGeneratorAfterDeath(actualPosition);
                continue;
            }

            if (potentialField.getX() > boundary.upperRight().getX()){
                Vector2d actualPosition = new Vector2d(boundary.lowerLeft().getX(), potentialField.getY());;
                betterFields.put(actualPosition, betterFieldDuration);
                addFieldToGeneratorAfterDeath(actualPosition);
                continue;
            }

            betterFields.put(potentialField, betterFieldDuration);
            addFieldToGeneratorAfterDeath(potentialField);
        }
    }

    private void addFieldToGeneratorAfterDeath(Vector2d pos){
        if (!grasses.containsKey(pos) && betterFields.containsKey(pos)){
            //if grass wasn't on the map and wasn't a betterField before we are sure that it is in normalVector list
            randomPositionGenerator.addNearCorpsesPosition(pos, true);
        }
        //if betterFields already have this position, or there is grass on this position, we can do nothing :)
    }

    public void createNewGrass(){
        Iterator<Map.Entry<Vector2d, Integer>> iterator = betterFields.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Vector2d, Integer> entry = iterator.next();
            Vector2d pos = entry.getKey();
            Integer daysLeft = entry.getValue();
            daysLeft--;
            betterFields.put(pos, daysLeft);
            if (daysLeft <= 0){
                iterator.remove();
                randomPositionGenerator.addNormalEmptyPosition(pos, true);
            }
        }

        randomPositionGenerator.prepareRandomPositionGenerator(grassPerDay);
        for(Vector2d grassPosition : randomPositionGenerator) {
            grasses.put(grassPosition, new Grass(grassPosition));
        }
    }

    @Override
    public boolean isBetterField(Vector2d pos){
        return betterFields.containsKey(pos);
    }
}
