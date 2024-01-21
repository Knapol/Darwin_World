package agh.ics.oop.model;

import agh.ics.oop.model.map.MapType;
import agh.ics.oop.model.worldElements.AnimalBehavior;

public record Settings(
        int mapWidth,
        int mapHeight,
        int numberOfAnimals,
        int genomeSize,
        int minMutations,
        int maxMutations,
        AnimalBehavior animalBehavior,
        int startingEnergy,
        int moveCost,
        int minEnergyToBreed,
        int energyUseForBreeding,
        int grassCount,
        int grassEnergy,
        int grassPerDay,
        MapType mapType,
        int betterFieldDuration
) {}
