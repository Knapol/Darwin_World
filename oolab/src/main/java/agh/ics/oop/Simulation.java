package agh.ics.oop;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private boolean simulationIsRunning = true;

    public Simulation(List<Vector2d> startPositions, WorldMap map){
        this.map = map;
        createAndPlaceAnimals(startPositions);
    }

    private void createAndPlaceAnimals(List<Vector2d> startPositions){
        for (Vector2d pos : startPositions){
            Animal newAnimal = new Animal(pos, map);
            try {
                map.place(newAnimal);
                this.animals.add(newAnimal);
            }
            catch (PositionAlreadyOccupiedException e){
                System.out.println("Animal was not placed on the map");
            }
        }
    }
    public void run() {
        while (true){
            try {
                if (simulationIsRunning) {
                    newSimulationDay();
                } else {
                    Thread.sleep(100);
                }
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void newSimulationDay() throws InterruptedException{
        Iterator<Animal> it = animals.iterator();
        while (it.hasNext()){
            Animal animal = it.next();
            if (animal.getEnergy() <= 0){
                it.remove();
            }
            map.move(animal);
        }

        if (animals.isEmpty()){
            System.out.println("All dead sadge");
            return;
        }

        map.breedAnimals(animals);

        map.eatGrass();
        map.createNewGrass();
        map.mapChanged("New frame");
        Thread.sleep(500);
    }

    public void pause(){
        simulationIsRunning = false;
    }

    public void start(){
        simulationIsRunning = true;
    }

    public boolean getSimulationState(){
        return simulationIsRunning;
    }
}
