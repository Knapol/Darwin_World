package agh.ics.oop.model.map;

import agh.ics.oop.model.Boundary;
import agh.ics.oop.model.Vector2d;

public class ForestedEquators extends AbstractWorldMap {

    public ForestedEquators(int width, int height, int genomeSize, int startingEnergy, int minEnergyToBreed, int moveCost){
        super(width, height, genomeSize, startingEnergy, minEnergyToBreed, moveCost);

        //        RandomPositionGenerator randomPositionGenerator = new RandomPositionGenerator(
//                (int)Math.sqrt(10*numberOfGrass),
//                (int)Math.sqrt(10*numberOfGrass),
//                numberOfGrass
//        );
//
//        for(Vector2d grassPosition : randomPositionGenerator) {
//            grasses.put(grassPosition, new Grass(grassPosition));
//        }
    }
}
