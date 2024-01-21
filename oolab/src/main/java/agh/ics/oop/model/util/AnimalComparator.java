package agh.ics.oop.model.util;


import agh.ics.oop.model.worldElements.Animal;

import java.util.Comparator;

public class AnimalComparator implements Comparator<Animal> {

    @Override
    public int compare(Animal animal1, Animal animal2){
        if (animal1.getID() == animal2.getID()){
            return 0;
        }

        int energyComparison = animal1.getEnergy() - animal2.getEnergy();
        if (energyComparison != 0){
            return energyComparison;
        }

        int daysAliveComparison = animal1.getDaysAlive() - animal2.getDaysAlive();
        if (daysAliveComparison != 0){
            return daysAliveComparison;
        }

        int numberOfChildrenComparison = animal1.getNumberOfChildren() - animal2.getNumberOfChildren();
        if (numberOfChildrenComparison != 0){
            return numberOfChildrenComparison;
        }

        return Math.random() >= 0.5 ? 1 : -1;
    }
}
