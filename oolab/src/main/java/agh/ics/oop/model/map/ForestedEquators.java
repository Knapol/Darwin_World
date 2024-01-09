package agh.ics.oop.model.map;

import agh.ics.oop.model.*;

public class ForestedEquators extends AbstractWorldMap {

    private final RandomPositionGenerator randomPositionGenerator;

    public ForestedEquators(int width, int height, int genomeSize, int startingEnergy, int minEnergyToBreed, int moveCost, int grassCount, int grassEnergy){
        super(width, height, genomeSize, startingEnergy, minEnergyToBreed, moveCost, grassEnergy);

        this.randomPositionGenerator = new RandomPositionGenerator(width, height, grassCount);
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

    @Override
    protected void updateRandomPositionGenerator(Vector2d pos){
        randomPositionGenerator.addEmptyPosition(pos);
    }

    public void createNewGrass(){
        randomPositionGenerator.prepareRandomPositionGenerator(1);
        for(Vector2d grassPosition : randomPositionGenerator) {
            grasses.put(grassPosition, new Grass(grassPosition));
        }
    }
}
