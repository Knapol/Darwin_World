package agh.ics.oop;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.*;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private boolean simulationIsRunning = true;

    public Simulation(List<Vector2d> startPositions, WorldMap map){
        this.map = map;
        createAndPlaceAnimals();
    }

    private void createAndPlaceAnimals(){
        Random random = new Random();
        for (int i=0; i<map.getStartingNumberOfAnimals(); i++){
            int x = random.nextInt(map.getWidth());
            int y = random.nextInt(map.getHeight());

            Vector2d position = new Vector2d(x, y);
            Animal newAnimal = new Animal(position, map);

            map.place(newAnimal);
            this.animals.add(newAnimal);
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
