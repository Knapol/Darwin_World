package agh.ics.oop.model;

public record Settings(
        int mapWidth,
        int mapHeight,
        int genomeSize,
        int startingEnergy,
        int moveCost,
        int minEnergyToBreed,
        int grassCount,
        int grassEnergy
) {}
