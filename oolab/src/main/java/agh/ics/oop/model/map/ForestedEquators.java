package agh.ics.oop.model.map;

import agh.ics.oop.model.*;

public class ForestedEquators extends AbstractWorldMap {

    private final RandomFEPositionGenerator randomPositionGenerator;

    public ForestedEquators(Settings settings){
        super(settings);

        this.randomPositionGenerator = new RandomFEPositionGenerator(settings.mapWidth(), settings.mapHeight(), settings.grassCount());
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

    @Override
    public boolean isBetterField(Vector2d pos){
        int equatorLength = (int) Math.round(this.getHeight() * 0.2);
        int equatorStart = (int) Math.round((this.getHeight() - equatorLength) * 0.5);
        int equatorEnd = equatorStart + equatorLength - 1;

        return pos.getY() >= equatorStart && pos.getY() <= equatorEnd;
    }
}
