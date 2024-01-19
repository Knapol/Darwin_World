package agh.ics.oop.model.map;

import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface WorldMap {
    void place(Animal animal);

    void move(Animal animal);

    void breedAnimals(List<Animal> animalsList);

    void eatGrass();

    boolean isOccupied(Vector2d position);

    WorldElement objectAt(Vector2d position);

    ArrayList<WorldElement> getElements();

    UUID getId();

    Boundary getCurrentBounds();

    void handleAnimalDeath(Animal animal);

    void mapChanged(String message);

    void createNewGrass();

    boolean isBetterField(Vector2d pos);

    int getStartingEnergy();

    int getGenomeSize();

    int getMinEnergyToBreed();

    int getMoveCost();

    int getStartingNumberOfAnimals();

    int getWidth();

    int getHeight();

    int getMinMutations();

    int getMaxMutations();

    AnimalBehavior getAnimalBehavior();

    int getEnergyUseForBreeding();

    int getAnimalCount();

    int getNextAnimalID();

    int getGrassCount();

    int getEmptyFieldsCount();
}
