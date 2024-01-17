package agh.ics.oop.model;

import agh.ics.oop.model.map.MapType;

public record Settings(
        int mapWidth,
        int mapHeight,
        int numberOfAnimals,
        int genomeSize,
        int startingEnergy,
        int moveCost,
        int minEnergyToBreed,
        int grassCount,
        int grassEnergy,
        MapType mapType
) {}
